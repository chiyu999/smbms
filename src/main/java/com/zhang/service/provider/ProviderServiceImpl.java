package com.zhang.service.provider;

import com.zhang.dao.BaseDao;
import com.zhang.dao.provider.ProviderDaoImpl;
import com.zhang.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProviderServiceImpl implements ProviderService{

    private ProviderDaoImpl providerDao;
    public ProviderServiceImpl(){
        providerDao = new ProviderDaoImpl();
    }

    /**
     * 根据条件获取用户列表
     * @param proCode
     * @param proName
     * @return
     * @throws SQLException
     */
    @Override
    public List<Provider> getProviderList(String proCode, String proName) throws SQLException {
        Connection connection = null;
        List<Provider> providerList = null;
        try {
            connection = BaseDao.getConnection();
            providerList = providerDao.getProviderList(connection, proCode, proName);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return providerList;
    }

    /**
     * 根据proId获取供应商详细信息
     * @param proId
     * @return
     * @throws SQLException
     */
    @Override
    public Provider getProviderById(int proId) throws SQLException {
        Connection connection = null;
        Provider provider = null;
        try {
            connection = BaseDao.getConnection();
            provider = providerDao.getProviderById(connection, proId);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return provider;
    }

    /**
     * 增加供应商
     * @param provider
     * @return
     * @throws SQLException
     */
    @Override
    public boolean addProvider(Provider provider) throws SQLException {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int i = providerDao.addProvider(connection, provider);
            connection.commit();
            if (i > 0){
                flag = true;
            }
        }catch (Exception e){
            connection.rollback();
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    /**
     * 更改供应商信息
     * @param provider
     * @return
     * @throws SQLException
     */
    @Override
    public boolean modifyProviderById(Provider provider) throws SQLException {
        boolean flag = false;
        Connection connection = null;
        if (provider != null){
            System.out.println("ProviderServiceImp[l=======>modifyProviderById");
            try {
                connection = BaseDao.getConnection();
                connection.setAutoCommit(false);
                int i = providerDao.modifyProviderById(connection, provider);
                connection.commit();
                if (i > 0) {
                    flag = true;
                }
            }catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
            }finally {
                BaseDao.closeResource(connection,null,null);
            }
        }
        return flag;
    }

    /**
     * 删除供应商
     * @param providerId
     * @return
     * @throws SQLException
     */
    @Override
    public boolean deleteProviderById(int providerId) throws SQLException {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int i = providerDao.deleteProviderById(connection, providerId);
            connection.commit();
            if (i > 0) {
                flag = true;
            }
        }catch (SQLException e){
            connection.commit();
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }
}
