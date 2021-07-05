package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.mapper.ModuleMapper;
import com.xxxx.crm.mapper.PermissionMapper;
import com.xxxx.crm.mapper.RoleMapper;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Autowired(required = false)
    private PermissionMapper permissionMapper;

    @Autowired(required = false)
    private ModuleMapper moduleMapper;


    public List<Map<String,Object>> queryRoles(Integer userId){
        return roleMapper.selectAllRoles(userId);
    }

    /**
     * 查询所有的角色
     * @param query
     * @return
     */
    public Map<String,Object> queryRole(RoleQuery query){
        //实例化map
        Map<String,Object> map=new HashMap<String,Object>();
        //开始分页
        PageHelper.startPage(query.getPage(),query.getLimit());
        //查询所有的数据
        List<Role> rlist = roleMapper.selectByParams(query);
        //实例化pageInfo
        PageInfo<Role> pageInfo=new PageInfo<>(rlist);

        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());

        return  map;
    }



    public void addRole(Role role){
        //验证，非空，角色不能重复
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色不能为空");
        Role temp=roleMapper.selectRoleByName(role.getRoleName());
        //
        AssertUtil.isTrue(temp!=null,"角色已经存在");
        //默认值
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //添加是否成功
        AssertUtil.isTrue(insertSelective(role)<1,"添加角色失败");
        //
        //addGrant(role.getId(),Integer [] mIds);
    }

    /**
     * 验证
     * @param roleId
     * @param mids
     */
    public void addGrant(Integer roleId,Integer [] mids){
       //roleyan验证
        Role temp = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(roleId==null || temp==null,"待授权的角色不存在");
        //统计一下角色的权限数量
        int count=permissionMapper.countPermissionByRoleId(roleId);
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionByRoleId(roleId)!=count,"权限分配失败");
        }
        //收集所有的权限数据
        if(mids!=null && mids.length>0){
            //准备存储权限的集合
            List<Permission> permissions=new ArrayList<Permission>();
            for (Integer mid: mids) {
                //实例化对象Permission
                Permission permission=new Permission();
                permission.setRoleId(roleId);
                permission.setModuleId(mid);
                //默认参数
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                //寻找当前module信息，根据mid
                Module module=moduleMapper.selectByPrimaryKey(mid);
                //赋值
                permission.setAclValue(module.getOptValue());
               //存储到集合
                permissions.add(permission);
            }
            //批量添加验证
            AssertUtil.isTrue(permissionMapper.insertBatch(permissions)!=permissions.size(),"授权失败");

        }

    }
    public void changeRole(Role role){
        //验证，非空，角色不能重复
        AssertUtil.isTrue(role.getId()==null || roleMapper.selectByPrimaryKey(role.getId())==null,"待修改角色不存在");
        //
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名称");
        //角色名称不能重复
        Role temp = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp!=null &&!(temp.getId().equals(role.getId())),"角色已经存在");
        //默认值
        role.setUpdateDate(new Date());
        //添加是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"修改角色失败");

    }
}
