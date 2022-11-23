package com.zhang.servlet.bill;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.zhang.pojo.Bill;
import com.zhang.pojo.Provider;
import com.zhang.pojo.User;
import com.zhang.service.bill.BillService;
import com.zhang.service.bill.BillServiceImpl;
import com.zhang.service.provider.ProviderService;
import com.zhang.service.provider.ProviderServiceImpl;
import com.zhang.utils.Constants;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BillServlet extends HttpServlet {
    private BillService billService;
    public BillServlet(){
        billService = new BillServiceImpl();
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && method.equals("query")){
            this.query(req, resp);
        }else if (method != null && method.equals("add")){
            this.add(req, resp);
        }else if (method != null && method.equals("getproviderlist")){
            this.getProviderList(req, resp);
        }else if(method != null && method.equals("delbill")) {
            this.delBillById(req, resp);
        }else if(method != null && method.equals("view")){
            this.getBillById(req,resp,"billview.jsp");
        }else if(method != null && method.equals("modify")){
            this.getBillById(req,resp,"billmodify.jsp");
        }else if(method != null && method.equals("modifysave")){
            this.modify(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * 查询订单列表
     * @param req
     * @param resp
     * @throws SQLException
     * @throws ServletException
     * @throws IOException
     */
    public void query(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<Provider> providerList = new ArrayList<Provider>();
        ProviderService providerService = new ProviderServiceImpl();
        providerList = providerService.getProviderList("","");
        req.setAttribute("providerList", providerList);

        String queryProductName = req.getParameter("queryProductName");
        String queryProviderId = req.getParameter("queryProviderId");
        String queryIsPayment = req.getParameter("queryIsPayment");

        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }

        List<Bill> billList = new ArrayList<Bill>();
        Bill bill = new Bill();
        if(StringUtils.isNullOrEmpty(queryIsPayment)){
            bill.setIsPayment(0);
        }else{
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if(StringUtils.isNullOrEmpty(queryProviderId)){
            bill.setProviderId(0);
        }else{
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        req.setAttribute("billList", billList);
        req.setAttribute("queryProductName", queryProductName);
        req.setAttribute("queryProviderId", queryProviderId);
        req.setAttribute("queryIsPayment", queryIsPayment);
        req.getRequestDispatcher("billlist.jsp").forward(req,resp);
    }

    /**
     * 添加订单
     * @param req
     * @param resp
     * @throws Exception
     */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        String billCode = req.getParameter("billCode");
        String productName = req.getParameter("productName");
        String productDesc = req.getParameter("productDesc");
        String productUnit = req.getParameter("productUnit");
        String productCount = req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment = req.getParameter("isPayment");

        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2,BigDecimal.ROUND_DOWN)); //保留小数点后两位，直接去掉多余的位数
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2,BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());
        boolean flag = false;
        flag = billService.add(bill);
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }else{
            req.getRequestDispatcher("billadd.jsp").forward(req, resp);
        }
    }

    /**
     * 删除订单
     * @param req
     * @param resp
     * @throws Exception
     */
    public void delBillById(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        String id = req.getParameter("billid");
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(id)){
            boolean flag = billService.deleteBillById(id);
            if(flag){//删除成功
                resultMap.put("delResult", "true");
            }else{//删除失败
                resultMap.put("delResult", "false");
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    /**
     * 获取供应商列表
     * @param req
     * @param resp
     * @throws Exception
     */
    public void getProviderList(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        System.out.println("getproviderlist ========================= ");

        List<Provider> providerList = new ArrayList<Provider>();
        ProviderService providerService = new ProviderServiceImpl();
        providerList = providerService.getProviderList("","");
        //把providerList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(providerList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    /**
     * 获取订单详细信息
     * @param request
     * @param response
     * @param url
     * @throws ServletException
     * @throws IOException
     */
    public void getBillById(HttpServletRequest request, HttpServletResponse response,String url) throws Exception {
        String id = request.getParameter("billid");
        if(!StringUtils.isNullOrEmpty(id)){
            BillService billService = new BillServiceImpl();
            Bill bill = null;
            bill = billService.getBillById(id);
            request.setAttribute("bill", bill);
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * 修改订单信息
     * @param req
     * @param resp
     * @throws Exception
     */
    public void modify(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        System.out.println("modify===============");
        String id = req.getParameter("id");
        String productName = req.getParameter("productName");
        String productDesc = req.getParameter("productDesc");
        String productUnit = req.getParameter("productUnit");
        String productCount = req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment = req.getParameter("isPayment");

        Bill bill = new Bill();
        bill.setId(Integer.valueOf(id));
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2,BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2,BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));

        bill.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());
        boolean flag = false;
        BillService billService = new BillServiceImpl();
        flag = billService.modify(bill);
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }else{
            req.getRequestDispatcher("billmodify.jsp").forward(req, resp);
        }
    }

}
