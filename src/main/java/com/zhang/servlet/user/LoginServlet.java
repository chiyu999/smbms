package com.zhang.servlet.user;

import com.zhang.pojo.User;
import com.zhang.service.user.UserService;
import com.zhang.service.user.UserServiceImpl;
import com.zhang.utils.Constants;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LoginServlet extends HttpServlet {
    //控制层，调用业务层的代码
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端获取用户名跟密码
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        //和数据库中的密码进行匹配，调用service层
        UserService userService = new UserServiceImpl();
        User loginUser = userService.login(userCode, userPassword); //这里已经把用户给查出来了
        if (loginUser != null){//查有此人，说明数据库中有数据匹配了，可以登录
            //将用户信息存入session
            req.getSession().setAttribute(Constants.USER_SESSION,loginUser);
            System.out.println(loginUser.toString());
            //跳转到首页，显示当前登录的用户
            resp.sendRedirect("jsp/frame.jsp");
        }else { //登录不成功，
            //页面跳转，（login.jsp)，带出提示信息
            req.setAttribute("error","用户名或密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doGet(req, resp);
    }
}
