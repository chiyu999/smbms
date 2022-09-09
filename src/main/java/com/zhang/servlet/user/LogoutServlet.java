package com.zhang.servlet.user;

import com.zhang.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //移除用户session
        req.getSession().removeAttribute(Constants.USER_SESSION);
        if (req.getSession().getAttribute(Constants.USER_SESSION) == null){
            System.out.println("成功移除用户session,返回登录页面");
        }
        //返回登录页面
        resp.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
