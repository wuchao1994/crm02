package com.xxxx.crm.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping("login")
    @ResponseBody
    public ResultInfo sayLogin(String userName, String userPwd) {
        //实例化对象
        ResultInfo resultInfo = new ResultInfo();
        //登录操作
        UserModel userModel = userService.doLogin(userName, userPwd);
        //将返回的对象赋值给resultInfo
        resultInfo.setResult(userModel);
        //返回目标对象
        return resultInfo;
    }

    @RequestMapping("toPasswordPage")
    public String sayPwd() {
        return "user/password";
    }

    @RequestMapping("toSettingPage")
    public String toSettingPage(HttpServletRequest req) {
        //重当前Cookie中获取对象userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //根据userId查询用户信息
        User user = userService.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user", user);
        //转发
        return "user/setting";
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo sayUpdate(User user) {
        userService.updateUser(user);
        return success("修改信息成功了");
    }

    @RequestMapping("updatePassword")
    @ResponseBody
    public ResultInfo sayPassword(HttpServletRequest req, String oldPassword, String newPassword, String confirmPwd) {
        //登录操作
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //修改密码
        userService.updateUserPwd(userId, oldPassword, newPassword, confirmPwd);
        //返回目标对象
        return success("修改密码OK");
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryAllUser(UserQuery query) {
        Map<String, Object> map = new HashMap<>();
        //分页
        PageHelper.startPage(query.getPage(), query.getLimit());
        //查询
        List<User> ulist = userService.queryUserByParam(query);
        /* 开发分页 */
        PageInfo<User> plist = new PageInfo<User>(ulist);
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", plist.getTotal());
        map.put("data", plist.getList());
        //
        return map;
    }

    @RequestMapping("index")
    public String index() {
        return "user/user";
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo sayAdd(User user) {
        userService.addUser(user);
        return success("用户添加成功");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo sayDels(Integer[] ids) {
        userService.removeUserByIds(ids);
        return success("用户删除成功");
    }

    @RequestMapping("addOrUpdatePage")
    public String sayAddOrUpdate(Integer id, Model model) {
        if (id != null) {
            //查询用户信息
            User user = userService.selectByPrimaryKey(id);
            //存储
            model.addAttribute("user", user);
        }
        return "user/add_update";
    }
}


