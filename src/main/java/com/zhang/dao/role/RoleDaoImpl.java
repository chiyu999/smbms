package com.zhang.dao.role;

import com.zhang.dao.BaseDao;
import com.zhang.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    /**
     * 获取角色列表
     * @param connection
     * @return
     * @throws Exception
     */
    @Override
    public List<Role> getRoleList(Connection connection) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Role> roleList = new ArrayList<Role>();
        if(connection != null) {
            String sql = "select * from smbms_role";
            Object[] params = {};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roleList.add(role);
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return roleList;
    }
    @Test
    public void test() throws Exception {
        RoleDaoImpl roleDao = new RoleDaoImpl();
        Connection connection = BaseDao.getConnection();
        List<Role> roleList = roleDao.getRoleList(connection);
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }
}
