package com.zhang.service.bill;

import com.zhang.dao.BaseDao;
import com.zhang.dao.bill.BillDao;
import com.zhang.dao.bill.BillDaoImpl;
import com.zhang.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillServiceImpl implements BillService {
    private BillDao billDao;

    public BillServiceImpl() {
        billDao = new BillDaoImpl();
    }

    /**
     * 根据查询条件获取到订单列表
     * @param queryBill
     * @return
     * @throws SQLException
     */
    @Override
    public List<Bill> getBillList(Bill queryBill) throws SQLException {
        Connection connection = null;
        List<Bill> billList = null;
        try {
            connection = BaseDao.getConnection();
            billList = billDao.getBillList(connection, queryBill);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return billList;

    }

    /**
     * 添加订单
     * @param bill
     * @return
     * @throws Exception
     */
    @Override
    public boolean add(Bill bill) throws Exception {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            if(billDao.add(connection,bill) > 0) {
                flag = true;
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }finally{
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    /**
     * 删除订单
     * @param delId
     * @return
     */
    @Override
    public boolean deleteBillById(String delId) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if(billDao.deleteBillById(connection, delId) > 0)
                flag = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    /**
     * 获取订单信息
     * @param id
     * @return
     */
    @Override
    public Bill getBillById(String id) {
        Bill bill = null;
        Connection connection = null;
        try{
            connection = BaseDao.getConnection();
            bill = billDao.getBillById(connection, id);
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            bill = null;
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return bill;
    }

    /**
     * 修改订单信息
     * @param bill
     * @return
     */
    @Override
    public boolean modify(Bill bill) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if(billDao.modify(connection,bill) > 0){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }


}
