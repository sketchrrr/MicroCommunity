package com.java110.report.bmo.reportOweFee.impl;

import com.java110.core.annotation.Java110Transactional;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.intf.report.IReportOweFeeInnerServiceSMO;
import com.java110.po.reportFee.ReportOweFeePo;
import com.java110.report.bmo.reportOweFee.ISaveReportOweFeeBMO;
import com.java110.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("saveReportOweFeeBMOImpl")
public class SaveReportOweFeeBMOImpl implements ISaveReportOweFeeBMO {

    @Autowired
    private IReportOweFeeInnerServiceSMO reportOweFeeInnerServiceSMOImpl;

    /**
     * 添加小区信息
     *
     * @param reportOweFeePo
     * @return 订单服务能够接受的报文
     */
    @Java110Transactional
    public ResponseEntity<String> save(ReportOweFeePo reportOweFeePo) {

        reportOweFeePo.setOweId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_oweId));
        int flag = reportOweFeeInnerServiceSMOImpl.saveReportOweFee(reportOweFeePo);

        if (flag > 0) {
            return ResultVo.createResponseEntity(ResultVo.CODE_OK, "保存成功");
        }

        return ResultVo.createResponseEntity(ResultVo.CODE_ERROR, "保存失败");
    }

}
