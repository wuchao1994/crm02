package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.mapper.UserMapper;
import com.xxxx.crm.mapper.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;

    /**
     * 1)验证用户名，密码
     * 2）用户是否存在
     * 3）用户密码是否正确
     * 4）返回UserModel
     * @param userName
     * @param userPwd
     * @return
     */

    public UserModel doLogin(String userName, String userPwd){
        //1)验证用户名，密码
        checkUser(userName,userPwd);
        //2）用户是否存在
        User temp = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(temp ==null ,"用户不存在或者用户已经注销");
        //3）用户密码是否正确
        checkUserPwd(temp.getUserPwd(),userPwd);
        //构建返回对象
        return builderInfo(temp);
    }

    /**
     *
     * @param user
     * @return
     */
    private UserModel builderInfo(User user) {
        UserModel userModel=new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     *
     * @param userPwd 数据中查询的密码
     * @param uPwd 新输入的密码
     */
    private void checkUserPwd(String userPwd, String uPwd) {
        //加密
        uPwd=Md5Util.encode(uPwd);
       //对比
        AssertUtil.isTrue(!userPwd.equals(uPwd),"密码不正确");
    }

    /**
     *
     * @param userName
     * @param userPwd
     */
    private void checkUser(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户密码不能为空");
    }

    /**
     * 1)原始密码非空，原始密码要正确
     *
     * 2)新密码（非空），不能和原始密码一样，
     *
     * 3）确认密码和新密码一致
     */


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPwd(Integer userId,String oldPassword,String newPassword,String confirmPwd){
        //获取对象信息
        User user = userMapper.selectByPrimaryKey(userId);
        //验证用户参数
        checkUserPassword(user,oldPassword,newPassword,confirmPwd);
        //设定密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //修改
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败了");
    }

    private void checkUserPassword(User user, String oldPassword, String newPassword, String confirmPwd) {
        // user对象 非空验证
        AssertUtil.isTrue(null == user, "用户未登录或不存在！");
        // 原始密码 非空验证
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "请输入原始密码！");
        // 原始密码要与数据库中的密文密码保持一致
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))), "原始密码不正确！");
        // 新密码 非空校验
        AssertUtil.isTrue(StringUtils.isBlank(newPassword), "请输入新密码！");
        // 新密码与原始密码不能相同
        AssertUtil.isTrue(oldPassword.equals(newPassword), "新密码不能与原始密码相同！");
        // 确认密码 非空校验
        AssertUtil.isTrue(StringUtils.isBlank(confirmPwd), "请输入确认密码！");
        // 新密码要与确认密码保持一致
        AssertUtil.isTrue(!(newPassword.equals(confirmPwd)), "新密码与确认密码不一致！");
    }


    /**
     * 查询所有的销售人员信息
     * @return
     */
    public List<Map<String,Object>> queryAllSales(){
       return userMapper.selectAllSales();
    }


    /**
     * 根据条件查询用户信息
     */

    public List<User> queryUserByParam(UserQuery query){
       return userMapper.selectByParams(query);
    }


    /**
     * 添加用户
     *  1. 参数校验
     *      用户名 非空 唯一性
     *      邮箱   非空
     *      手机号 非空  格式合法
     *  2. 设置默认参数
     *      isValid 1
     *      creteDate   当前时间
     *      updateDate  当前时间
     *      userPwd 123456 -> md5加密
     *  3. 执行添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        //1验证
        checkUserParam(user.getUserName(),user.getEmail(),user.getPhone());
        //默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //密码加密
        user.setUserPwd(Md5Util.encode("123456"));
        //判断是否成功
        AssertUtil.isTrue(userMapper.insertUserByReturnKey(user)<1,"用户添加失败");

        //userId,roleIds;
        System.out.println(user.getId()+"<<");
        //批量添加用户和角色的关系表数据
        relaionUserRole(user.getId(),user.getRoleIds());
    }

    /**
     *
     * @param id 用户id
     * @param roleIds 角色id的字符串
     */
    //1,2,3;
    private void relaionUserRole(Integer id, String roleIds) {
        //统计角色
        int count = userRoleMapper.countUserRoles(id);
        if(count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRolesByUid(id)!=count,"角色分配失败");
        }
        if(StringUtils.isNotBlank(roleIds)){
            //准备一个List
            List<UserRole> urlist=new ArrayList<UserRole>();
            //遍历
            for (String rid: roleIds.split(",")) {
                UserRole ur=new UserRole();
                ur.setUserId(id);
                ur.setRoleId(Integer.parseInt(rid));
                ur.setCreateDate(new Date());
                ur.setUpdateDate(new Date());
                //添加容器
                urlist.add(ur);
            }
            //insert
            AssertUtil.isTrue(userRoleMapper.insertBatch(urlist)!=urlist.size(),"角色分配失败了");
        }
    }

    /**
     *
     * @param userName 非空唯一
     * @param email 非空，
     * @param phone  非空，合法手机号
     */
    private void checkUserParam(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        //用户名唯一
        User temp = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(temp!=null,"用户名已经存在");
        //
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"请输入合法的手机号");
    }


    /**
     * 批量删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUserByIds(Integer [] ids){
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择目标数据");
        AssertUtil.isTrue(userMapper.deleteBatch(ids)<1,"删除失败了");
    }

    /**
     * 更新用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        // 1. 参数校验
        // 通过id查询用户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 判断对象是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        // 验证参数
        checkUserParam(user.getUserName(),user.getEmail(),user.getPhone());
        // 2. 设置默认参数
        temp.setUpdateDate(new Date());
        // 3. 执行更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户更新失败！");
        //用户更新中间表
        Integer userId=userMapper.selectUserByName(user.getUserName()).getId();
        relaionUserRole(userId,user.getRoleIds());
    }


    /**
     * 删除一条记录
     * @param userId
     */
    public void deleteUser(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        //
        AssertUtil.isTrue(user==null,"待删除记录不存在");
        int count = userRoleMapper.countUserRoles(userId);
        if(count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRolesByUid(userId)!=count,"用户角色删除失败");

        }
        user.setIsValid(0);
        //更新，删除
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"用户记录删除失败");
    }
}
