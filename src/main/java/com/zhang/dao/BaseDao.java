package com.zhang.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//基础工具类
public class BaseDao {
    private static String driver;
    private static String url;
    private static String userName;
    private static String password;
    /**
     * 在这个类加载的时候就进行数据库连接资源的初始化
     */
    static {
        init();
    }

    /**
     * 初始化数据库连接参数
     */
    public static void init(){
        Properties properties = new Properties();
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        userName = properties.getProperty("userName");
        password = properties.getProperty("password");
    }

    /**
     * 获取数据库连接
     * @return Connection
     */
    public static Connection getConnection(){
        Connection connection = null;
        try {
            //1、获取数据库连接驱动
            Class.forName(driver);
            connection = DriverManager.getConnection(url,userName,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 查询公共类
     * @param connection
     * @param pstm
     * @param rs
     * @param sql
     * @param parms
     * @return ResultSet
     */
    public static ResultSet execute(Connection connection, PreparedStatement pstm,ResultSet rs,String sql,Object[] parms){
        try {
            //预编译sql
            pstm = connection.prepareStatement(sql);
            //设置预编译的参数
            for (int i = 0; i < parms.length; i++) {
                //占位符从1开始，而数组从0开始
                pstm.setObject(i+1,parms[i]);
            }
            rs = pstm.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * 编写增删改公共类
     * @param connection
     * @param pstm
     * @param sql
     * @param params
     * @return int
     */
    public static int execute(Connection connection,PreparedStatement pstm, String sql,Object[] params) {
        int updateRows = 0;
        try {
            pstm = connection.prepareStatement(sql);
            for(int i = 0; i < params.length; i++){
                pstm.setObject(i+1, params[i]);
            }
            updateRows = pstm.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return updateRows;
    }
    /**
     * 释放资源
     * @param connection
     * @param pstm
     * @param rs
     * @return boolean
     */
    public static boolean closeResource(Connection connection,PreparedStatement pstm,ResultSet rs){
        boolean flag = true;
        if(rs != null){
            try {
                rs.close();
                //如果关闭不成功的话可以设置为空，gc回收机制会把他回收，不要造成资源浪费
                rs = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(pstm != null){
            try {
                pstm.close();
                pstm = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(connection != null){
            try {
                connection.close();
                connection = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }

}
