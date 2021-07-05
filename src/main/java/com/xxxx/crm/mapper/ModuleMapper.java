package com.xxxx.crm.mapper;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.dto.TreeModel;
import com.xxxx.crm.vo.Module;

import java.util.List;
import java.util.Map;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    //查询所有的资源，roleId
    //id,name,pid;
    public List<TreeModel> selectAllModule();
    //查询所有的资源
     List<Module> selectAllModules();
}