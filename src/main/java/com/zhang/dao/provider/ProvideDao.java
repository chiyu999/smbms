package com.zhang.dao.provider;

import com.zhang.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProvideDao {
    /**
     * 查询供应商列表
     * @param connection
     * @param proCode
     * @param proName
     * @return
     * @throws SQLException
     */
    public List<Provider> getProviderList(Connection connection,String proCode,String proName) throws SQLException;

    /**
     * 根据Id查询供应商
     * @param connection
     * @param proId
     * @return
     * @throws SQLException
     */
    public Provider getProviderById(Connection connection,int proId) throws SQLException;

    /**
     * 添加供应商
     * @param provider
     * @return
     * @throws SQLException
     */
    public int addProvider(Connection connection,Provider provider) throws SQLException;

    /**
     * 根据Id更改供应商信息
     * @param connection
     * @param provider
     * @return
     * @throws SQLException
     */
    public int modifyProviderById(Connection connection,Provider provider) throws SQLException;

    public int deleteProviderById(Connection connection,int providerId) throws SQLException;
}
