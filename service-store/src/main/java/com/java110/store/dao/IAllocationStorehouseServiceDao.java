package com.java110.store.dao;


import com.java110.utils.exception.DAOException;


import java.util.List;
import java.util.Map;

/**
 * 仓库调拨组件内部之间使用，没有给外围系统提供服务能力
 * 仓库调拨服务接口类，要求全部以字符串传输，方便微服务化
 * 新建客户，修改客户，删除客户，查询客户等功能
 *
 * Created by wuxw on 2016/12/27.
 */
public interface IAllocationStorehouseServiceDao {

    /**
     * 保存 仓库调拨信息
     * @param businessAllocationStorehouseInfo 仓库调拨信息 封装
     * @throws DAOException 操作数据库异常
     */
    void saveBusinessAllocationStorehouseInfo(Map businessAllocationStorehouseInfo) throws DAOException;

    /**
     * 查询仓库调拨信息（business过程）
     * 根据bId 查询仓库调拨信息
     * @param info bId 信息
     * @return 仓库调拨信息
     * @throws DAOException DAO异常
     */
    List<Map> getBusinessAllocationStorehouseInfo(Map info) throws DAOException;

    /**
     * 保存 仓库调拨信息 Business数据到 Instance中
     * @param info
     * @throws DAOException DAO异常
     */
    void saveAllocationStorehouseInfoInstance(Map info) throws DAOException;

    /**
     * 保存调拨记录
     * @param info
     * @throws DAOException DAO异常
     */
    void saveAllocationStorehouseInfo(Map info) throws DAOException;

    /**
     * 查询仓库调拨信息（instance过程）
     * 根据bId 查询仓库调拨信息
     * @param info bId 信息
     * @return 仓库调拨信息
     * @throws DAOException DAO异常
     */
    List<Map> getAllocationStorehouseInfo(Map info) throws DAOException;

    /**
     * 修改仓库调拨信息
     * @param info 修改信息
     * @throws DAOException DAO异常
     */
    void updateAllocationStorehouseInfoInstance(Map info) throws DAOException;

    /**
     * 查询仓库调拨总数
     *
     * @param info 仓库调拨信息
     * @return 仓库调拨数量
     */
    int queryAllocationStorehousesCount(Map info);

}
