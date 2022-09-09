package com.zhang.dao.user;

import com.zhang.pojo.User;

import java.sql.Connection;
import java.util.List;

public interface UserDao {
    /**
     * 得到登录的用户
     * @param connection
     * @param userCode
     * @return User
     * @throws Exception
     */
    public User getLoginUser(Connection connection, String userCode)throws Exception;
    /**
     * 修改当前用户密码
     * @param connection
     * @param id
     * @param pwd
     * @return int
     * @throws Exception
     */
    public int updatePwd(Connection connection, int id, String pwd)throws Exception;

    /**
     * 根据用户名或用户角色查询用户总数
     * @param connection
     * @return  int count
     * @throws Exception
     */
    public int getUserCount(Connection connection,String username,int userRole) throws Exception;

    /**
     * 获取用户列表
     * @param connection
     * @param username
     * @param userRole
     * @param currentPageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<User> getUserList(Connection connection,String username,int userRole,int currentPageNo,int pageSize) throws Exception;

    /**
     * 通过UserId删除User
     * @param connection
     * @param id
     * @return
     */
    public int deleteUserById(Connection connection,int id)throws Exception;

    /**
     * 根据Id查询用户信息
     * @param id
     * @return
     * @throws Exception
     */
    public User queryUserById(Connection connection,int id) throws Exception;

    /**
     * 增加用户
     * @param connection
     * @param user
     * @return
     * @throws Exception
     */
    public int addUser(Connection connection,User user) throws Exception;

    /**
     * 查询用户表的所有userCode
     * @param connection
     * @return
     * @throws Exception
     */
    public List<String> getUserCodeList(Connection connection) throws Exception;

    /**
     * 根据用户id更改用户信息
     * @param connection
     * @param user
     * @return
     * @throws Exception
     */
    public int modifyUserById(Connection connection,User user) throws Exception;
}
