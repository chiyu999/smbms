package com.zhang.service.provider;

import com.zhang.pojo.Provider;

import java.sql.SQLException;
import java.util.List;

public interface ProviderService {
    /**
     * 根据条件获取供应商列表
     * @param proCode
     * @param proName
     * @return
     * @throws SQLException
     */
    public List<Provider> getProviderList(String proCode,String proName) throws SQLException;

    /**
     * 根据id获取供应商详细信息
     * @param proId
     * @return
     * @throws SQLException
     */
    public Provider getProviderById(int proId) throws SQLException;

    /**
     * 添加供应商
     * @param provider
     * @return
     * @throws SQLException
     */
    public boolean addProvider(Provider provider) throws SQLException;

    /**
     * 根据用户id修改用户信息
     * @param provider
     * @return
     * @throws SQLException
     */
    public boolean modifyProviderById(Provider provider) throws SQLException;

    /**
     * 删除供应商
     * @param providerId
     * @return
     * @throws SQLException
     */
    public boolean deleteProviderById(int providerId) throws SQLException;


}
