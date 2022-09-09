package com.zhang.service.Role;

import com.zhang.dao.BaseDao;
import com.zhang.dao.role.RoleDao;
import com.zhang.dao.role.RoleDaoImpl;
import com.zhang.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService {
    private RoleDao roleDao;
    public RoleServiceImpl(){
        roleDao = new RoleDaoImpl();
    }

    /**
     * 获取角色列表
     * @return
     */
    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;
        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return roleList;
    }

    @Test
    public void test1() {
        List<Role> roleList = new RoleServiceImpl().getRoleList();
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }

}
