package com.zhang.dao.provider;

import com.mysql.jdbc.StringUtils;
import com.zhang.dao.BaseDao;
import com.zhang.pojo.Provider;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ProviderDaoImpl implements ProvideDao{
    /**
     * 根据条件查询供应商列表
     * @param connection
     * @param proCode
     * @param proName
     * @return
     * @throws SQLException
     */
    @Override
    public List<Provider> getProviderList(Connection connection,String proCode,String proName) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Object> list = new ArrayList<>(); //存放参数
        List<Provider> providerList = new ArrayList<>();
        if (connection != null){
            StringBuffer sql = new StringBuffer();
            sql.append("select * from smbms_provider p");
            if (!StringUtils.isNullOrEmpty(proCode)){
                sql.append(" where p.proCode like ?");
                list.add("%"+proCode+"%");
                if (!StringUtils.isNullOrEmpty(proName)){
                    sql.append(" and p.proName like ?");
                    list.add("%"+proName+"%");
                }
            }else {
                if (!StringUtils.isNullOrEmpty(proName)){
                    sql.append(" where p.proName like ?");
                    list.add("%"+proName+"%");
                }
            }

            Object[] params = list.toArray();
            for (Object param : params) {
                System.out.println(param);
            }
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            System.out.println("providerDao ====> sql=====>"+sql.toString());
            while (rs.next()){
                Provider provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                //provider.setCreatedBy(rs.getInt("createdBy"));
                provider.setCreationDate(rs.getDate("creationDate"));
                //provider.setModifyBy(rs.getInt("modifyBy"));
                //provider.setModifyDate(rs.getDate("modifyDate"));
                providerList.add(provider);
            }
            BaseDao.closeResource(null, pstm, rs);

        }
        return providerList;
    }

    /**
     * 根据id查询供应商
     * @param connection
     * @param proId
     * @return
     * @throws SQLException
     */
    @Override
    public Provider getProviderById(Connection connection, int proId) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Provider provider = null;
        if (connection != null){
            String sql = "select * from smbms_provider p where p.id = ?";
            Object[] params = {proId};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            while (rs.next()){
                provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProFax(rs.getString("proFax"));
            }

        }
        return provider;
    }

    /**
     * 添加供应商
     * @param provider
     * @return
     * @throws SQLException
     */
    @Override
    public int addProvider(Connection connection,Provider provider) throws SQLException {
        int result = 0;
        PreparedStatement pstm = null;
        if (connection!= null){
            String sql = "insert into smbms_provider" +
                    "(proCode,proName,proDesc,proContact,proPhone,proAddress,proFax,createdBy,creationDate)" +
                    "values (?,?,?,?,?,?,?,?,?)";
            Object[] params = {
                    provider.getProCode(),
                    provider.getProName(),
                    provider.getProDesc(),
                    provider.getProCode(),
                    provider.getProPhone(),
                    provider.getProAddress(),
                    provider.getProFax(),
                    provider.getCreatedBy(),
                    new Date()
            };
            result = BaseDao.execute(connection,pstm,sql,params);
        }
        return result;
    }

    /**
     * 根据Id修改供应商信息
     * @param connection
     * @param provider
     * @return
     * @throws SQLException
     */
    @Override
    public int modifyProviderById(Connection connection, Provider provider) throws SQLException {
        int result = 0;
        PreparedStatement pstm = null;
        if (connection != null) {
            System.out.println("ProviderDaoImp[l=======>modifyProviderById");
            String sql = "update smbms_provider" +
                    " set proCode=?,proName=?,proContact=?,proPhone=?,proAddress=?,proFax=?,proDesc=? where id=?";
            Object[] params = {
                    provider.getProCode(),
                    provider.getProName(),
                    provider.getProContact(),
                    provider.getProPhone(),
                    provider.getProAddress(),
                    provider.getProFax(),
                    provider.getProDesc(),
                    provider.getId()};
            result = BaseDao.execute(connection,pstm,sql,params);
            BaseDao.closeResource(null, pstm, null);
            }
        return result;
    }

    /**
     * 根据Id删除供应商
     * @param connection
     * @param providerId
     * @return
     * @throws SQLException
     */
    @Override
    public int deleteProviderById(Connection connection, int providerId) throws SQLException {
        int result = 0;
        PreparedStatement pstm = null;
        if (connection!= null) {
            String sql = "delete from smbms_provider" +
                    " where id=?";
            Object[] params = {providerId};
            result = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return result;
    }

    @Test
    public void test() throws SQLException {
        Connection connection = BaseDao.getConnection();
//        if (connection != null){
//            System.out.println("connection != null");
//            ProviderDaoImpl providerDao = new ProviderDaoImpl();
//            List<Provider> providerList = providerDao.getProviderList(connection, "BJ_GYS001", "北京");
//            for (Provider provider : providerList) {
//                System.out.println(provider.getProCode());
//            }
//        }
//        if (connection != null){
//            Provider providerById = new ProviderDaoImpl().getProviderById(connection, 1);
//            System.out.println(providerById.toString());
//        }

    }
}
