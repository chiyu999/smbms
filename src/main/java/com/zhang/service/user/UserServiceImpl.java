package com.zhang.service.user;

import com.zhang.dao.BaseDao;
import com.zhang.dao.user.UserDao;
import com.zhang.dao.user.UserDaoImpl;
import com.zhang.pojo.User;
import com.zhang.servlet.user.UserServlet;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户业务层
 */
public class UserServiceImpl implements UserService{
    //业务层要调用dao层，所以我们要引入dao层
    private UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    /**
     * 获取登录用户
     * @param userCode
     * @param userPassword
     * @return user
     */
    @Override
    public User login(String userCode, String userPassword) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }

        //判断密码是否相同
        if (user != null){
            if (!user.getUserPassword().equals(userPassword)){
                user = null;
            }
        }
        return user;
    }

    /**
     * 根据用户id修改密码
     * @param id
     * @param pwd
     * @return boolean true==修改成功  false==修改失败
     */
    @Override
    public boolean updatePwd(int id, String pwd) {
        Connection connection = null;
        //标志位，用于标志是否修改密码成功
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            //修改密码
            if (userDao.updatePwd(connection, id, pwd)>0){
                flag = true;
                return flag;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    /**
     * 获取用户总数
     * @param username
     * @param userRole
     * @return
     */
    @Override
    public int getUserCount(String username, int userRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, username, userRole);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return count;
    }

    /**
     * 获取用户列表
     * @param username
     * @param userRole
     * @param currentPageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public List<User> getUserList(String username, int userRole, int currentPageNo, int pageSize) throws Exception {
        Connection connection = null;
        List<User> userList = null;
        System.out.println("UserService 获得的要查询的名字====>"+ username);
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, username, userRole, currentPageNo, pageSize);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return userList;
    }

    /**
     * 通过id删除user
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteUserById(int id) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if(userDao.deleteUserById(connection, id) > 0){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public User queryUserById(int id) throws Exception {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.queryUserById(connection, id);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;

    }

    /**
     * 判断添加用户是否成功
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public boolean addUser(User user) throws Exception {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false); //开启JDBC事务管理
            int updateRows = userDao.addUser(connection, user);
            connection.commit();
            if (updateRows >0){
                flag = true;
            }
        }catch (Exception e){
            //设置事务回滚
            try{
                connection.rollback();
            }catch (SQLException se){
                se.printStackTrace();
            }

        }finally {
            BaseDao.closeResource(connection,null,null);

        }
        return flag;
    }

    /**
     * 判断UserCode是否已经存在
     * @param userCodee
     * @return
     * @throws Exception
     */
    @Override
    public boolean isUserCodeExist(String userCodee) throws Exception {
        //存放结果
        boolean flag = false;
        Connection connection = null;
        //调用dao层获取userCode
        try {
            connection = BaseDao.getConnection();
            List<String> userCodeList = userDao.getUserCodeList(connection);
            if(userCodeList.contains(userCodee)) {
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    /**
     * 根据id修改相关用户信息
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public boolean modifyUserById(User user) throws Exception {
        System.out.println("in UserServiceImpl modifyUserById");
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int i = userDao.modifyUserById(connection,user);
            connection.commit();
            if( i> 0){
                flag = true;
            }
        }catch (Exception e){
            try {
                connection.rollback();
            }catch (SQLException se){
                se.printStackTrace();
            }
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }


    @Test
    public void test() throws Exception {
        //测试 判断用户角色是否已经在数据库存在
        //System.out.println(new UserServiceImpl().isUserCodeExist("admin"));


        //测试根据id查询用户名
//        UserServiceImpl userService = new UserServiceImpl();
//        try {
//            User user = userService.queryUserById(1);
//            System.out.println(user.getUserName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //测试添加用户
        User user = new User();
        user.setUserCode("dahuang");
        user.setUserName("dahuang");
        user.setUserPassword("1111111");
        user.setGender(1);
        user.setBirthday(new Date());
        user.setPhone("11122233344");
        user.setAddress("广东省汕头市潮南区");
        user.setUserRole(3);
        user.setCreatedBy(1);
        user.setCreationDate(new Date());

        try {
            System.out.println(new UserServiceImpl().addUser(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
