package com.xxxx.crm.controller;

import com.xxxx.crm.annotation.RequiredPermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;

    @Autowired
    private UserService userService;

    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }

    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdate(Integer id, Model mode) {
        //修改和添加主要的区别是表单中是否有ID,有id修改操作，否则添加
        if (id != null) {
            //查询销售机会对象
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //存储
            mode.addAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }


    @RequestMapping("list")
    @ResponseBody
    @RequiredPermission(code = "101001")
    public Map<String, Object> sayList(SaleChanceQuery saleChanceQuery,Integer flag,HttpServletRequest req) {
        //判断
        if(flag!=null && flag == 1){
            //获取当前用户的userId
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
            //分配人
            saleChanceQuery.setAssignMan(userId);
        }
        //查询
        return saleChanceService.querySaleChanceByParam(saleChanceQuery);
    }


    @RequestMapping("save")
    @ResponseBody
    public ResultInfo sayAdd(SaleChance saleChance, HttpServletRequest req) {
        //从获取userId
       // Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用方法查询
        //String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        String trueName = CookieUtil.getCookieValue(req, "trueName");
        System.out.println(trueName+"<<");
        //指定创建人
        saleChance.setCreateMan(trueName);
        saleChance.setCreateDate(new Date());
        System.out.println(saleChance.getAssignMan()+"<<");
        //添加
        saleChanceService.saveSaleChance(saleChance);
        return success("营销机会添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo sayUpdate(SaleChance saleChance, HttpServletRequest req) {
        //修改
        saleChanceService.changeSaleChance(saleChance);
        return success("营销机会添加成功");
    }

    @RequestMapping("dels")
    @ResponseBody
    public ResultInfo sayDels(Integer[] ids) {
        System.out.println(Arrays.toString(ids)+"<<<controller");
        saleChanceService.removeSaleChance(ids);
        return success("批量删除营销机会成功");
    }

    @RequestMapping("querySales")
    @ResponseBody
    public List<Map<String,Object>> querySales(){
        return userService.queryAllSales();
    }
}
