package com.zhang.dao.role;

import com.zhang.pojo.Role;

import java.sql.Connection;
import java.util.List;

public interface RoleDao {
    /**
     * 获取角色列表
     * @return
     */
    public List<Role> getRoleList(Connection connection) throws Exception;
}
