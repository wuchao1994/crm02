package com.xxxx.crm.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dto.TreeModel;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.vo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;



    @Autowired
    private ModuleService moduleService;



    @RequestMapping("queryModule")
    @ResponseBody
    public List<TreeModel> sayAllModule(Integer roleId){
        return moduleService.queryAllModule(roleId);
    }



    @RequestMapping("queryRoles")
    @ResponseBody
    public List<Map<String,Object>> sayListRole(Integer userId){
        List<Map<String,Object>> mlist=new ArrayList<>();
        return  roleService.queryRoles(userId);
    }


    @RequestMapping("toAddGrandPage")
    public String sayAddGrand(Model model,Integer  roleId){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }


    @RequestMapping("index")
    public String index(){
        return "role/role";
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> sayList(RoleQuery query){
        return  roleService.queryRole(query);
    }


    @RequestMapping("toAddOrUpdatePage")
    public String toUpdate(Integer rid, Model model){
        if(rid!=null){
            Role role=roleService.selectByPrimaryKey(rid);
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo toPage(Integer rid){
        roleService.deleteByPrimaryKey(rid);
        return success("删除成功了");
    }



    @RequestMapping("save")
    @ResponseBody
    public ResultInfo toAdd(Role role){
        roleService.addRole(role);
        return success("添加角色成功了");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo toUpdate(Role role){
        roleService.changeRole(role);
        return success("修改角色成功了");
    }



    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId,Integer [] mIds){
        roleService.addGrant(roleId,mIds);
        return  success("授权成功了");
    }
}
