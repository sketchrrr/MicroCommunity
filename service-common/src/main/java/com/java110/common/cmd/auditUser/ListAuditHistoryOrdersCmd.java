package com.java110.common.cmd.auditUser;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.event.cmd.Cmd;
import com.java110.core.event.cmd.CmdEvent;
import com.java110.dto.itemRelease.ItemReleaseDto;
import com.java110.dto.oaWorkflow.OaWorkflowDto;
import com.java110.dto.oaWorkflow.WorkflowDto;
import com.java110.dto.purchase.PurchaseApplyDto;
import com.java110.dto.audit.AuditUser;
import com.java110.intf.common.IOaWorkflowActivitiInnerServiceSMO;
import com.java110.intf.common.IResourceEntryStoreInnerServiceSMO;
import com.java110.intf.oa.IOaWorkflowInnerServiceSMO;
import com.java110.intf.store.IPurchaseApplyInnerServiceSMO;
import com.java110.utils.exception.CmdException;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import com.java110.vo.ResultVo;
import com.java110.vo.api.resourceOrder.ApiResourceOrderDataVo;
import com.java110.vo.api.resourceOrder.ApiResourceOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "auditUser.listAuditHistoryOrders")
public class ListAuditHistoryOrdersCmd extends Cmd {

    @Autowired
    private IResourceEntryStoreInnerServiceSMO resourceEntryStoreInnerServiceSMOImpl;


    @Autowired
    private IOaWorkflowActivitiInnerServiceSMO oaWorkflowUserInnerServiceSMOImpl;

    @Autowired
    private IOaWorkflowInnerServiceSMO oaWorkflowInnerServiceSMOImpl;

    @Autowired
    private IPurchaseApplyInnerServiceSMO purchaseApplyInnerServiceSMOImpl;
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "storeId", "必填，请填写商户ID");
        Assert.hasKeyAndValue(reqJson, "userId", "必填，请填写用户ID");
        Assert.hasKeyAndValue(reqJson, "row", "必填，请填写每页显示数");
        Assert.hasKeyAndValue(reqJson, "page", "必填，请填写页数");

        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {


        String userId = context.getReqHeaders().get("user-id");
        String storeId = context.getReqHeaders().get("store-id");

        OaWorkflowDto oaWorkflowDto = new OaWorkflowDto();
        oaWorkflowDto.setState(OaWorkflowDto.STATE_COMPLAINT);
        oaWorkflowDto.setFlowType(OaWorkflowDto.FLOW_TYPE_PURCHASE_APPLY);
        List<OaWorkflowDto> oaWorkflowDtos = oaWorkflowInnerServiceSMOImpl.queryOaWorkflows(oaWorkflowDto);

        if (oaWorkflowDtos == null || oaWorkflowDtos.size() < 1) {
            return;
        }
        List<String> flowIds = new ArrayList<>();
        for (OaWorkflowDto tmpOaWorkflowDto : oaWorkflowDtos) {
            flowIds.add(WorkflowDto.DEFAULT_PROCESS + tmpOaWorkflowDto.getFlowId());
        }

        AuditUser auditUser = new AuditUser();
        auditUser.setProcessDefinitionKeys(flowIds);
        auditUser.setUserId(userId);
        auditUser.setStoreId(storeId);
        auditUser.setPage(reqJson.getInteger("page"));
        auditUser.setRow(reqJson.getInteger("row"));

        long count = oaWorkflowUserInnerServiceSMOImpl.getDefinitionKeysUserHistoryTaskCount(auditUser);

        List<JSONObject> datas = null;

        if (count > 0) {
            datas = oaWorkflowUserInnerServiceSMOImpl.getDefinitionKeysUserHistoryTasks(auditUser);
            //刷新 表单数据
            refreshFormData(datas, reqJson,storeId);
        } else {
            datas = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, datas);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        context.setResponseEntity(responseEntity);
    }

    private void refreshFormData(List<JSONObject> datas, JSONObject paramIn,String storeId) {

        List<String> ids = new ArrayList<>();
        for (JSONObject data : datas) {
            ids.add(data.getString("id"));
        }
        if (ids.size() < 1) {
            return;
        }

        //查询 投诉信息
        PurchaseApplyDto purchaseApplyDto = new PurchaseApplyDto();
        purchaseApplyDto.setStoreId(storeId);
        purchaseApplyDto.setApplyOrderIds(ids.toArray(new String[ids.size()]));
        List<PurchaseApplyDto> tmpPurchaseApplyDtos = purchaseApplyInnerServiceSMOImpl.queryPurchaseApplyAndDetails(purchaseApplyDto);

        if (tmpPurchaseApplyDtos == null || tmpPurchaseApplyDtos.size() < 1) {
            return;
        }

        for (PurchaseApplyDto tmpPurchaseApplyDto : tmpPurchaseApplyDtos) {
            switch (tmpPurchaseApplyDto.getState()) {
                case "1000":
                    tmpPurchaseApplyDto.setStateName("待审核");
                    break;
                case "1001":
                    tmpPurchaseApplyDto.setStateName("审核中");
                    break;
                case "1002":
                    tmpPurchaseApplyDto.setStateName("已审核");
                    break;
            }
            if (tmpPurchaseApplyDto.getResOrderType().equals("10000")) {
                tmpPurchaseApplyDto.setResOrderTypeName("采购申请");
            } else {
                tmpPurchaseApplyDto.setResOrderTypeName("出库申请");
            }


        }
        for (JSONObject data : datas) {
            for (PurchaseApplyDto form : tmpPurchaseApplyDtos) {
                if (data.getString("id").equals(form.getApplyOrderId())) {
                    data.putAll(BeanConvertUtil.beanCovertJson(form));
                }
            }
        }
    }
}
