package com.zhang.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.zhang.pojo.Role;
import com.zhang.pojo.User;
import com.zhang.service.Role.RoleService;
import com.zhang.service.Role.RoleServiceImpl;
import com.zhang.service.user.UserService;
import com.zhang.service.user.UserServiceImpl;
import com.zhang.utils.Constants;
import com.zhang.utils.PageSupport;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 实现Servlet复用
 * User的操作都在这个Servlet中实现
 */
public class UserServlet extends HttpServlet {
    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && method.equals("savepwd")){ //更改密码
            this.updatePwd(req,resp);
        }else if (method != null && method.equals("pwdmodify")){ //ajax 旧密码验证---获得session用户密码
            this.getPwdByUserId(req, resp);
        }else if (method != null && method.equals("query")){ //查询所有用户信息，展示，分页
            this.query(req, resp);
        }else if (method != null && method.equals("deluser")){ //删除用户
            this.delUserById(req, resp);
        }else if (method != null && method.equals("view")){ //显示用户详细信息
            this.queryUserById(req, resp,"userview.jsp");
        }else if (method != null && method.equals("add")){ // 增加用户
            this.addUser(req, resp);
        }else if (method != null && method.equals("getrolelist")){ //用户添加页面用户角色下拉框数据
            this.getRoleList(req, resp);
        }else if (method != null && method.equals("ucexist")){ //userCode是否存在
            this.isUserCodeExists(req, resp);
        }else if (method != null && method.equals("modify")){ //更新信息跳转页面
            this.queryUserById(req, resp,"usermodify.jsp");
        }else if (method != null && method.equals("modifyexe")){
            this.modifyUserById(req, resp);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * 修改用户密码
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    private void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //因为是根据id修改密码，在登录的时候已经把用户的所有信息都存到userSession中了，可以从session中获取当前用户的id
        //获取用户
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        //从前端获取新密码
        String newpassword = req.getParameter("newpassword");
        //设立标志位，默认修改不成功
        boolean flag = false;
        //判断获取到的用户是否为空，新密码是否为空
        if((User)o != null && newpassword != null && newpassword.length()>0){
            //调用service层方法实现业务
            UserServiceImpl userService = new UserServiceImpl();
            //把更新密码的结果设置给flag
//            System.out.println("UserServlet"+((User) o).getId());
            flag = userService.updatePwd(((User) o).getId(), newpassword);
            if (flag){ //如果为true，则说明修改密码成功
                //密码修改成功，需要重新登录，提示修改密码成功，移除用户session
                req.setAttribute("message","密码修改成功，请返回登录页面，使用新密码重新登录");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                //密码修改失败
                req.setAttribute("message","密码修改失败");
            }
        }else { //
            req.setAttribute("message","新密码有问题");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
    }

    /**
     * 根据id从session中获取密码
     * @param req
     * @param resp
     */
    public void getPwdByUserId(HttpServletRequest req, HttpServletResponse resp){
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");
       //万能的Map，一切东西都可以存放
        Map<String, String> resultMap = new HashMap<String, String>();
        if (o == null){ //Session过期了
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){//如果前端获取的参数是null或空串
            resultMap.put("result","error");
        }else {
            String userPassword = ((User) o).getUserPassword();
            if (userPassword.equals(oldpassword)){ //旧密码正确
                resultMap.put("result","true");
            }else { //旧密码错误
                resultMap.put("result","false");

            }
        }
        //把Map里的值去除以json格式的字符串返回给前端
        //设置返回响应头
        resp.setContentType("application/json");
        //获取流
        PrintWriter outPrintWriter = null;
        try {
            outPrintWriter = resp.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //alibab的JSONArray工具类，转换格式使用
        //={key，value}  然后传递给前端  result
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    /**
     * 页面数据的查询
     * @param req
     * @param resp
     * @throws Exception
     */
    public void query(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //获取参数

        //要查询的名字
        String queryUserName = req.getParameter("queryname");
        //要查询的用户角色 转换成int
        String temp = req.getParameter("queryUserRole");
        //要查询的页面
        String pageIndex = req.getParameter("pageIndex");
        //当前页 第一次进入页面肯定是第一页，页面大小是固定的
        int currentPageNo = 1;
        //页面大小
        int pageSize = 5;
        //要查询的用户角色
        int queryUserRole = 0;

        if(queryUserName == null){
            queryUserName = "";
        }
        System.out.println("前端获得的要查询的名字====>" + queryUserName);
        if (temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }
        if (pageIndex != null) {
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch (NumberFormatException e){
                resp.sendRedirect("error.jsp");
            }
        }

        //调用方法
        UserService userService = new UserServiceImpl();
        //获取用户总数量
        int totalUserCount = userService.getUserCount(queryUserName, queryUserRole);

        //工具类 分页支持
        PageSupport pageSupport = new PageSupport();
        //设置当前页码
        pageSupport.setCurrentPageNo(currentPageNo);
        //设置页面大小--》一个页面展示多少条数据
        pageSupport.setPageSize(pageSize);
        //设置用户总数量 ---》记录总条数
        pageSupport.setTotalCount(totalUserCount);

        //控制首页和尾页
        int totalPageCount = pageSupport.getTotalPageCount();
        if (currentPageNo < 1) { //当前页码小于1 ，强制等于1
            currentPageNo = 1;
        }else if (currentPageNo > totalPageCount){ //当前页码不可能大于总页数
            currentPageNo = totalPageCount;
        }
        //获取用户列表
        List<User> userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        //获取角色列表
        List<Role> roleList = new RoleServiceImpl().getRoleList();

        //返回前端数据

        //角色列表
        req.setAttribute("roleList",roleList);
        //用户列表
        req.setAttribute("userList",userList);
        //总页数
        req.setAttribute("totalPageCount",totalPageCount);
        //总记录数
        req.setAttribute("totalCount",totalUserCount);
        //当前页
        req.setAttribute("currentPageNo", currentPageNo);


        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);
        req.getRequestDispatcher("userlist.jsp").forward(req,resp);

    }

    /**
     * 根据id删除用户
     * @param req
     * @param resp
     */
    public void delUserById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uid = req.getParameter("uid");
        //要删除的用户id 默认为0
        int delUserId = 0;
        //如果要从前端传过来的参数不能转换为int类型的用户id，则给要删除的用户id赋值为0
        try {
            delUserId = Integer.parseInt(uid);
        }catch (Exception e){
            e.printStackTrace();
            delUserId = 0;
        }

        //万能的Map,用来存放返回前端的消息
        Map<String, String> map = new HashMap<>();
        if (delUserId <= 0){
            map.put("delResult","notexist");
        }else{
            UserService userService = new UserServiceImpl();
            boolean flag = userService.deleteUserById(delUserId);
            if (flag) {
                map.put("delResult", "true");
            }else {
                map.put("delResult", "false");
            }
        }

        //将map转换为JSON字符串返回
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        String result = JSONArray.toJSONString(map);
        writer.write(result);
        writer.flush();
        writer.close();
    }

    /**
     * 显示用户详细信息
     * @param req
     * @param resp
     */
    public void queryUserById(HttpServletRequest req, HttpServletResponse resp,String url) throws Exception {
        String uid = req.getParameter("uid");
        if (uid != null){
            UserService userService = new UserServiceImpl();

            User user = userService.queryUserById(Integer.parseInt(uid));

            req.setAttribute("user", user);

            req.getRequestDispatcher(url).forward(req,resp);

        }
    }

    /**
     * 添加用户
     * @param req
     * @param resp
     * @throws Exception
     */
    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //获取参数
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        //将参数封装成一个对象
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if(userService.addUser(user)){
            //转发到查询页面
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        }else {
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }


    }

    /**
     * 用户添加页面获取用户列表提供选择
     * @param req
     * @param resp
     * @throws Exception
     */
    public void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    /**
     * 判断UserCode 是否存在
     * @param req
     * @param resp
     * @throws Exception
     */
    public void isUserCodeExists(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //从前端获取参数
        String userCode = req.getParameter("userCode");
        //map 用来存放返回前端的参数
        Map<String, String> resultMap = new HashMap<String, String>();
        UserService userService = new UserServiceImpl();
        if(userService.isUserCodeExist(userCode)) {
            resultMap.put("userCode","exist");
        }else {
            resultMap.put("userCode","notExist");
        }
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    /**
     * 更新用户信息
     * @param req
     * @param resp
     * @throws Exception
     */
    public void modifyUserById(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //获取信息
        String userId = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String phone = req.getParameter("phone");
        String birthday = req.getParameter("birthday");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        //封装对象
        User user = new User();
        user.setId(Integer.valueOf(userId));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        System.out.println("userServlet--User======>"+user.toString());

        UserService userService = new UserServiceImpl();
        if (userService.modifyUserById(user)) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        }
    }

}


