package com.zhang.service.bill;

import com.zhang.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BillService {
    /**
     * 根据查询条件查询订单列表
     * @param queryBill
     * @return
     * @throws SQLException
     */
    public List<Bill> getBillList(Bill queryBill) throws SQLException;

    /**
     * 添加订单
     * @param bill
     * @return
     * @throws Exception
     */
    public boolean add(Bill bill) throws Exception;

    /**
     * 通过billId删除Bill
     * @param delId
     * @return
     */
    public boolean deleteBillById(String delId);

    /**
     * 通过billId获取Bill
     *
     * @param id
     * @return
     */
    public Bill getBillById(String id);

    /**
     * 修改订单信息
     *
     * @param bill
     * @return
     */
    public boolean modify(Bill bill);
}
