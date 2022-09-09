package com.zhang.dao.user;

import com.mysql.jdbc.StringUtils;
import com.zhang.dao.BaseDao;
import com.zhang.pojo.User;
import com.zhang.service.user.UserServiceImpl;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * UserDao实现类，进行数据库的查询操作
 */
public class UserDaoImpl implements UserDao {
    /**
     * 通过UserCode查询用户，用于登录页面对用户进行验证
     * @param connection
     * @param userCode
     * @return User
     * @throws Exception
     */
    @Override
    public User getLoginUser(Connection connection, String userCode) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;
        if (connection != null) {
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            rs = BaseDao.execute(connection,pstm,rs,sql,params);
            if (rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            //连接可能有业务，不能在这里关闭
            BaseDao.closeResource(null,pstm,rs);
        }
        return user;
    }

    /**
     * 修改当前用户密码
     * @param connection
     * @param id
     * @param pwd
     * @return int
     * @throws Exception
     */
    @Override
    public int updatePwd(Connection connection, int id, String pwd) throws Exception {
        PreparedStatement pstm = null;
        int execute = 0;
        if (connection != null){
            String sql = "update smbms_user set userPassword= ? where id = ?";
            //接收参数
            Object[] params = {pwd,id};
            execute = BaseDao.execute(connection, pstm, sql, params);
//            System.out.println("更改密码结果=====>"+execute);
            BaseDao.closeResource(null,pstm,null);
        }
        return execute;
    }

    /**
     * 根据用户名或者用户角色查询用户总数
     * @param connection
     * @param username
     * @param userRole
     * @return
     * @throws Exception
     */
    @Override
    public int getUserCount(Connection connection, String username, int userRole) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select COUNT(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");

            ArrayList<Object> list = new ArrayList<>();//用于存放参数

            if (!StringUtils.isNullOrEmpty(username)) { //如果输入用户名不为空
                sql.append(" and u.userName like ?");
                list.add("%" + username + "%"); //index = 0
            }
            if (userRole > 0){
                sql.append(" and u.userRole = ?");
                list.add(userRole); //index = 1
            }

            //list 转化为数组
            Object[] params = list.toArray();
            System.out.println("UserServiceImpl---->getUserCount:"+sql.toString());
            rs = BaseDao.execute(connection, pstm, rs,sql.toString(),params );
            if (rs.next()) {
                //从结果集中提取数量
                count = rs.getInt("count");
                BaseDao.closeResource(null, pstm, rs);
            }
        }
        return count;
    }

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
    @Override
    public List<User> getUserList(Connection connection, String username, int userRole,int currentPageNo,int pageSize) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Object> list = new ArrayList<>(); //存放参数
        List<User> userList = new ArrayList<>();
        System.out.println("UserDao获得的要查询的用户名=====>"+username);
        if (connection!= null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            if (!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.userName like ?");
                list.add("%"+username+"%");
            }
            if(userRole > 0){
                sql.append(" and u.userRole =?");
                list.add(userRole);
            }

            //在数据库中 分页使用limit startIndex pageSize
            //           当前页    （当前页-1）*页面大小
            // 0 5          1      01234
            // 6 5          2      56789
            //11 5          3
            //用于分页
            sql.append(" order by creationDate DESC limit ?,?");
            System.out.println("currentPageNo:"+currentPageNo);
            currentPageNo = (currentPageNo-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            //打印sql
            System.out.println("UserDaoImpl---->getUserList:"+sql.toString());
            //把查询的结果赋值给list
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setUserRole(rs.getInt("userRole"));
                user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(user);
            }
            BaseDao.closeResource(null, pstm, rs);
            }
        return userList;
        }

    /**
     * 根据id删除用户
     * @param connection
     * @param id
     * @return
     */
    @Override
    public int deleteUserById(Connection connection, int id) throws Exception{
        PreparedStatement pstm = null;
        int flag = 0;
        if (connection != null) {
            String sql = "delete from smbms_user where id = ?";
            Object[] params = {id};
            flag = BaseDao.execute(connection,pstm,sql, params);
            if (flag >0){
                List<Integer> userIdList = selectUserId();
                int i = 1;
                for (Integer integer : userIdList) {
                    if (integer.intValue() == id) {
                        break;
                    }else {
                        System.out.println("in updateUserId =====> userId"+integer.intValue()+"=====>updateId=====>"+i);
                        updateUserId(connection,integer,i);
                    }
                    i++;
                }
            }
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    /**
     * 根据Id查询用户详细信息
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public User queryUserById(Connection connection,int id) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = new User();
        if (connection != null) {
            String sql = "select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id=? and u.userRole = r.id";
            Object[] params = {id};
            rs = BaseDao.execute(connection,pstm, rs,sql,params);
            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
        }
        return user;
    }

    /**
     * 增加用户
     * @param connection
     * @param user
     * @return
     */
    public int addUser(Connection connection,User user) throws Exception{
        //存放结果
        int flag = 0;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String sql = "insert into smbms_user(userCode,userName,userPassword,gender,birthday,phone,address,userRole," +
                "createdBy,creationDate) values (?,?,?,?,?,?,?,?,?,?)";
        Object[] params = {user.getUserCode(),
                user.getUserName(),
                user.getUserPassword(),
                user.getGender(),
                user.getBirthday(),
                user.getPhone(),
                user.getAddress(),
                user.getUserRole(),
                user.getCreatedBy(),
                user.getCreationDate()
        };
        flag = BaseDao.execute(connection, pstm, sql, params);
        BaseDao.closeResource(null, pstm, rs);
        return flag;
    }

    /**
     * 查询所有UserCode
     * @param connection
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getUserCodeList(Connection connection) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<String> userCodeList = new ArrayList<String>();
        String sql = "select u.userCode from smbms_user u";
        Object[] params = {};
        rs = BaseDao.execute(connection, pstm, rs, sql, params);
        while (rs.next()) {
            userCodeList.add(rs.getString(1));
        }
        return userCodeList;
    }

    /**
     * 根据用户id修改用户信息
     * @param connection
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public int modifyUserById(Connection connection, User user) throws Exception {
        System.out.println("in UserDaoimpl modifyUserById");
        PreparedStatement pstm = null;
        int updateRows = 0;
        String sql = "update smbms_user set userName = ?,gender = ?,birthday = ?,phone = ?,address = ?,userRole = ? where id = ?";
        if(connection != null){
            try{
                Object[] params = {user.getUserName(),
                        user.getGender(),
                        user.getBirthday(),
                        user.getPhone(),
                        user.getAddress(),
                        user.getUserRole(),
                        user.getId()
                };
                updateRows = BaseDao.execute(connection,pstm,sql,params);
            }catch (Exception e){
                e.printStackTrace();
            }
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    /**
     * 获取所有用户id
     * @return
     * @throws SQLException
     */
    public List<Integer> selectUserId() throws SQLException {
        PreparedStatement pstm = null;
        Connection connection = BaseDao.getConnection();
        ResultSet rs = null;
        List<Integer> userIdList = new ArrayList<>(); //存放参数

        Object[] params = {};
        if (connection != null){
            String sql = "select u.id from smbms_user u";
            rs = BaseDao.execute(connection,pstm,rs, sql,params);
            while (rs.next()){
                userIdList.add(rs.getInt("id"));
            }
        }
        return userIdList;
    }

    /**
     * 更新用户id
     * @param connection
     * @param userId
     * @param updateId
     * @throws SQLException
     */
    public void updateUserId(Connection connection,int userId, int updateId) throws SQLException{
        PreparedStatement pstm = null;
        String sql = "update smbms_user set id = ? where id = ?";
        Object[] params = {updateId,userId};
        int execute = BaseDao.execute(connection, pstm, sql, params);
    }

    @Test
    public void test() throws Exception {

//    try {
//        User user = new UserDaoImpl().queryUserById(BaseDao.getConnection(), 1);
//        System.out.println(user.getUserName());
//        System.out.println(user.getUserRoleName());
//
//    } catch (Exception e) {
//        e.printStackTrace();
//    }


        //测试获取用户角色列表
//        List<String> userCodeList = new UserDaoImpl().getUserCodeList(BaseDao.getConnection());
//        for (String s : userCodeList) {
//            System.out.println(s);
//        }

        //测试 selectUserId() 获取用户id
//        List<Integer> integers = new UserDaoImpl().selectUserId();
//        for (Integer integer : integers) {
//            System.out.println(integer);
//        }

        //测试新增 重置id功能
        int i = new UserDaoImpl().deleteUserById(BaseDao.getConnection(), 15);
        System.out.println(i);
    }
}

