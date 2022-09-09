package com.zhang.dao.base;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//基础工具类
public class BaseDao {
    /**
     * 在这个类加载的时候就进行数据库连接资源的初始化
     */
    static {
        init();
    }

    private static String driver;
    private static String url;
    private static String userName;
    private static String password;

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
     */
    public static ResultSet execute(Connection connection, PreparedStatement pstm,ResultSet rs,String sql,Object[] parms){
        try {
            pstm = connection.prepareStatement(sql);
            for (int i = 0; i < parms.length; i++) {
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
     * @return
     */
    public static boolean closeResource(Connection connection,PreparedStatement pstm,ResultSet rs){
        boolean flag = true;
        if(rs != null){
            try {
                rs.close();
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
