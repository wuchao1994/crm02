package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.dto.TreeModel;
import com.xxxx.crm.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

    @Autowired
    private ModuleService moduleService;


    @RequestMapping("index")
    public String index(){
        return "module/module";
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> sayList(){
       return moduleService.queryAllModules();
    }

}
