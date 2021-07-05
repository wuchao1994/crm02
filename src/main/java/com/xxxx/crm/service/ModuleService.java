package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dto.TreeModel;
import com.xxxx.crm.mapper.ModuleMapper;
import com.xxxx.crm.mapper.PermissionMapper;
import com.xxxx.crm.vo.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Autowired(required = false)
    private ModuleMapper moduleMapper;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;



    public List<TreeModel> queryAllModule(Integer roleId){
       //查询所有的资源
      List<TreeModel> treeModels= moduleMapper.selectAllModule();
        //查询当前角色的资源
      List<Integer> roleHasMids=  permissionMapper.queryModuleByRoleId(roleId);
        //循环遍历，判断角色拥有那些资源，checked=true,checked=false;
        for (TreeModel module:treeModels) {
            if(roleHasMids.contains(module.getId())){
                module.setChecked(true);
            }
        }
        return  treeModels;
    }


    public Map<String,Object> queryAllModules(){
        Map<String,Object> map=new HashMap<>();
        //查询所有的数据
        List<Module> mlist = moduleMapper.selectAllModules();
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",mlist.size());
        map.put("data",mlist);
        //返回目标map
        return map;
    }
}
