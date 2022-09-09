package com.zhang.service.user;

import com.zhang.pojo.User;

import java.sql.Connection;
import java.util.List;


public interface UserService {
    /**
     * 用于用户登录
     * @param userCode
     * @param userPassword
     * @return User
     */
    public User login(String userCode, String userPassword);

    /**
     * 根据userId修改用户密码
     * @param id
     * @param pwd
     * @return boolean
     * @throws Exception
     */
    public boolean updatePwd(int id, String pwd);

    /**
     * 查询获得用户数量
     * @param username
     * @param userRole
     * @return count
     */
    public int getUserCount(String username,int userRole);

    /**
     * 获取用户列表
     * @param username
     * @param userRole
     * @param currentPageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<User> getUserList(String username, int userRole,int currentPageNo,int pageSize) throws Exception;

    /**
     * 通过id删除用户
     * @param id
     * @return
     */
    public boolean deleteUserById(int id);

    /**
     * 根据Id查询用户信息
     * @param id
     * @return
     * @throws Exception
     */
    public User queryUserById(int id) throws Exception;

    /**
     * 增加用户
     * @param user
     * @return boolean
     * @throws Exception
     */
    public boolean addUser (User user) throws Exception;

    /**
     * 判断UserCode是否已经存在
     * @return
     * @throws Exception
     */
    public boolean isUserCodeExist(String userCodee) throws Exception;

    /**
     * 根据用户id更改用户信息
     * @param user
     * @return
     * @throws Exception
     */
    public boolean modifyUserById(User user) throws Exception;
}
