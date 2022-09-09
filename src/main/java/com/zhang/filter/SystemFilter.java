package com.zhang.filter;

import com.zhang.pojo.User;
import com.zhang.utils.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SystemFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        //过滤器，从session中获取用户
        User user = (User) req.getSession().getAttribute(Constants.USER_SESSION);
        if (user == null){//用户已经注销或者未登录
            resp.sendRedirect("/error.jsp");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
