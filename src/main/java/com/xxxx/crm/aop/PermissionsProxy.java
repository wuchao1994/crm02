package com.xxxx.crm.aop;

import com.xxxx.crm.annotation.RequiredPermission;
import com.xxxx.crm.exceptions.NoAuthException;
import com.xxxx.crm.exceptions.NoLoginException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionsProxy {

    @Autowired
    HttpSession httpSession;


    @Around(value="@annotation(com.xxxx.crm.annotation.RequiredPermission)")
    public Object sayAop(ProceedingJoinPoint pjp) throws Throwable {
        //判断是否有权限
        List<String> permissions = ( List<String>)httpSession.getAttribute("permissions");
        //判断
        if(permissions== null || permissions.size()==0){
            throw new NoLoginException();
        }
        //有权限，获取权限码
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        RequiredPermission re = signature.getMethod().getDeclaredAnnotation(RequiredPermission.class);
        //
        if(!(permissions.contains(re.code()))){
            throw  new NoAuthException();
        }
        Object result = pjp.proceed();
        return  result;
    }
}
