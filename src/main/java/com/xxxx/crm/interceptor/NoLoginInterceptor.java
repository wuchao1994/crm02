package com.xxxx.crm.interceptor;

import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //判断是否登录
        //获取Cookie userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //没有登录，抛异常
        if(userId ==null || null==userService.selectByPrimaryKey(userId)){
            throw  new NoLoginException();
        }
        //放行
        return true;
    }
}
