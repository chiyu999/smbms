package com.zhang.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.zhang.pojo.Provider;
import com.zhang.pojo.User;
import com.zhang.service.provider.ProviderServiceImpl;
import com.zhang.utils.Constants;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProviderServlet extends HttpServlet {
    private ProviderServiceImpl providerService;
    public ProviderServlet(){
        providerService = new ProviderServiceImpl();

    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String method = req.getParameter("method");
        if (method != null && method.equals("query")) {
            this.query(req, resp);
        }else if (method != null && method.equals("view")){
            this.viewProviderById(req, resp,"providerview.jsp");
        }else if (method != null && method.equals("add")){
            this.addProvider(req, resp);
        }else if (method != null && method.equals("modify")){
            this.viewProviderById(req, resp,"providermodify.jsp");
        }else if (method != null && method.equals("modifysave")){
            this.modifyProviderById(req, resp);
        }else if (method != null && method.equals("delprovider")){
            this.deleteProviderById(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * 查询供应商列表
     * @param req
     * @param resp
     * @throws Exception
     */
    public void query(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        String queryProCode = req.getParameter("queryProCode");
        String queryProName = req.getParameter("queryProName");
        List<Provider> providerList = providerService.getProviderList(queryProCode, queryProName);
        req.setAttribute("providerList",providerList);
        req.setAttribute("queryProCode",queryProCode);
        req.setAttribute("queryProName",queryProName);
        req.getRequestDispatcher("providerlist.jsp").forward(req, resp);
    }

    /**
     * 根据Id查询用户详细信息
     * @param req
     * @param resp
     * @throws Exception
     */
    public void viewProviderById(HttpServletRequest req, HttpServletResponse resp,String url) throws Exception {
        String proId = req.getParameter("proid");
        if (proId != null){
            Provider provider = providerService.getProviderById(Integer.valueOf(proId));
            req.setAttribute("provider",provider);
            req.getRequestDispatcher(url).forward(req, resp);
        }else {
            req.getRequestDispatcher("error.jsp").forward(req, resp);
        }
    }

    /**
     * 添加用户
     * @param req
     * @param resp
     * @throws Exception
     */
    public void addProvider(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");
        Provider provider = new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);
        provider.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        boolean flag = providerService.addProvider(provider);
        if (flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }else {
            req.getRequestDispatcher("provideradd.jsp").forward(req, resp);
        }


    }

    /**
     * 根据ID修改供应商信息
     * @param req
     * @param resp
     * @throws Exception
     */
    public void modifyProviderById(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        String id = req.getParameter("id");
        System.out.println(id);
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");
        Provider provider = new Provider();
        if (!StringUtils.isNullOrEmpty(id)){
            provider.setId(Integer.parseInt(id));
        }
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);
        System.out.println(provider.toString());
        if (providerService.modifyProviderById(provider)){
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }else {
            req.getRequestDispatcher("providermodify.jsp").forward(req, resp);
        }

    }

    /**
     * 删除供应商
     * @param req
     * @param resp
     * @throws Exception
     */
    public void deleteProviderById(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        String proId = req.getParameter("proid");
        //要删除的供应商id 默认为0
        int delProviderId = 0;
        //如果要从前端传过来的参数不能转换为int类型的用户id，则给要删除的用户id赋值为0
        try {
            delProviderId = Integer.parseInt(proId);
        }catch (Exception e){
            e.printStackTrace();
            delProviderId = 0;
        }

        //万能的Map,用来存放返回前端的消息
        Map<String, String> map = new HashMap<>();
        if (delProviderId <= 0){
            map.put("delResult","notexist");
        }else{
            boolean flag = providerService.deleteProviderById(delProviderId);
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




}
