package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.service.PermissionService;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;
    /**
     * 登录页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    /**
     * 欢迎页面
     * @return
     */
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }


    /**
     * 后台页面
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest req){
        //获取Cookie,UserId
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //根据用户userId查询用户信息
        User user = userService.selectByPrimaryKey(userId);
        //存储数据
        req.setAttribute("user",user);
        //查询当前用户拥有的权限
       List<String> permissions= permissionService.queryPermissionByRoleByUserId(userId);
       //存储到session
        req.getSession().setAttribute("permissions",permissions);
        //跳转
        return "main";
    }
}
