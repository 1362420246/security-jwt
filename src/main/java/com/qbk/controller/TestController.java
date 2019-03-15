package com.qbk.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设置 Headers  Authorization : Bearer + token
 */
@RestController
public class TestController {


    /**
     *  @PreAuthorize： 在方法调用前，基于表达式计算结果来限制方法访问
        @PostAuthorize: 允许方法调用，但是如果表达式结果为fasle则抛出异常
        @PostFilter :允许方法调用，但必须按表达式过滤方法结果。
        @PreFilter: 允许方法调用，但必须在进入方法前过滤输入值

        例子：   角色需要的角色(即用户、管理员等)。注意，它不应该以“ROLE_”开头，因为这是自动插入的。
        //@PreAuthorize("hasRole('ADMIN')")  如果具有权限 ROLE_ADMIN 访问该方法
        //@PostFilter("returnObject.user.sex=='男' ") 将结果过滤，即选出性别为男的用户

        Spring Security 支持的所有SpEL表达式如下：
        authentication　　	用户认证对象
        denyAll　　	结果始终为false
        hasAnyRole(list of roles)　　	如果用户被授权指定的任意权限，结果为true
        hasRole(role)	如果用户被授予了指定的权限，结果 为true
        hasIpAddress(IP Adress)	用户地址
        isAnonymous()　　	是否为匿名用户
        isAuthenticated()　　	不是匿名用户
        isFullyAuthenticated　　	不是匿名也不是remember-me认证
        isRemberMe()　　	remember-me认证
        permitAll	始终true
        principal	用户主要信息对象

     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test")
    public String test(){
        return "测试";
    }


}
