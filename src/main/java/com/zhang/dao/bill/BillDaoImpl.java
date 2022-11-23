package com.zhang.dao.bill;

import com.mysql.jdbc.StringUtils;
import com.zhang.dao.BaseDao;
import com.zhang.pojo.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillDaoImpl implements BillDao{
    /**
     * 根据查询条件获取订单列表
     * @param connection
     * @param queryBill
     * @return
     * @throws SQLException
     */
    @Override
    public List<Bill> getBillList(Connection connection, Bill queryBill) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Bill> billList = new ArrayList<>();
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select b.*,p.proName as providerName from smbms_bill b, smbms_provider p where b.providerId = p.id");
            List<Object> list = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(queryBill.getProductName())) {
                sql.append(" and productName like ?");
                list.add("%" + queryBill.getProductName() + "%");
            }
            if (queryBill.getProviderId() > 0) {
                sql.append(" and providerId = ?");
                list.add(queryBill.getProviderId());
            }
            if (queryBill.getIsPayment() > 0) {
                sql.append(" and isPayment = ?");
                list.add(queryBill.getIsPayment());
            }
            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("providerName"));
                bill.setCreationDate(rs.getTimestamp("creationDate"));
                bill.setCreatedBy(rs.getInt("createdBy"));

                billList.add(bill);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return billList;
    }

    /**
     * 添加订单
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    @Override
    public int add(Connection connection, Bill bill) throws Exception {
        PreparedStatement pstm = null;
        int result = 0;
        if (null != connection) {
            String sql = "insert into smbms_bill (billCode,productName,productDesc," +
                    "productUnit,productCount,totalPrice,isPayment,providerId,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {bill.getBillCode(), bill.getProductName(), bill.getProductDesc(),
                    bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
                    bill.getProviderId(), bill.getCreatedBy(), bill.getCreationDate()};
            result = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return result;
    }

    /**
     * 删除订单
     * @param connection
     * @param delId
     * @return
     * @throws Exception
     */
    @Override
    public int deleteBillById(Connection connection, String delId) throws Exception {
        PreparedStatement pstm = null;
        int result = 0;
        if (null != connection) {
            String sql = "delete from smbms_bill where id=?";
            Object[] params = {delId};
            result = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return result;
    }

    /**
     * 通过id获取订单
     * @param connection
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public Bill getBillById(Connection connection, String id) throws Exception {
        Bill bill = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if (null != connection) {
            String sql = "select b.*,p.proName as providerName from smbms_bill b, smbms_provider p " +
                    "where b.providerId = p.id and b.id=?";
            Object[] params = {id};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            if (rs.next()) {
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("providerName"));
                bill.setModifyBy(rs.getInt("modifyBy"));
                bill.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return bill;
    }

    /**
     * 修改订单信息
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    @Override
    public int modify(Connection connection, Bill bill) throws Exception {
        int flag = 0;
        PreparedStatement pstm = null;
        if (null != connection) {
            String sql = "update smbms_bill set productName=?," +
                    "productDesc=?,productUnit=?,productCount=?,totalPrice=?," +
                    "isPayment=?,providerId=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {bill.getProductName(), bill.getProductDesc(),
                    bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
                    bill.getProviderId(), bill.getModifyBy(), bill.getModifyDate(), bill.getId()};
            flag = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    /**
     * 根据供应商id查询订单数量
     * @param connection
     * @param providerId
     * @return
     * @throws Exception
     */
    @Override
    public int getBillCountByProviderId(Connection connection, String providerId) throws Exception {
        int count = 0;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if (null != connection) {
            String sql = "select count(1) as billCount from smbms_bill where" +
                    "	providerId = ?";
            Object[] params = {providerId};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            if (rs.next()) {
                count = rs.getInt("billCount");
            }
            BaseDao.closeResource(null, pstm, rs);
        }

        return count;
    }
}
