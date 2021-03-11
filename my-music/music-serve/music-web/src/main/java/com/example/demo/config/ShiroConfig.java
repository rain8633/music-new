package com.example.demo.config;


import com.example.demo.realm.MyRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    /**
     * 配置拦截器
     * <p>
     * 定义拦截URL权限，优先级从上到下 1). anon : 匿名访问，无需登录 2). authc : 登录后才能访问 3). logout: 登出 4).
     * roles : 角色过滤器
     * <p>
     * URL 匹配风格 1). ?：匹配一个字符，如 /admin? 将匹配 /admin1，但不匹配 /admin 或 /admin/； 2).
     * *：匹配零个或多个字符串，如 /admin* 将匹配 /admin 或/admin123，但不匹配 /admin/1； 2).
     * **：匹配路径中的零个或多个路径，如 /admin/** 将匹配 /admin/a 或 /admin/a/b
     * <p>
     * 配置身份验证成功，失败的跳转路径
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

       filterChainDefinitionMap.put("/css/**", "anon"); // 静态资源匿名访问
        filterChainDefinitionMap.put("/image/*", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");

//
//        /*放开swagger*/
//        filterChainDefinitionMap.put("/swagger-ui.html","anon");
//        filterChainDefinitionMap.put("/swagger-resources/**","anon");
//        filterChainDefinitionMap.put("/v2/api-docs/**","anon");
//        filterChainDefinitionMap.put("/webjars/springfox-swagger-ui/**","anon");

//        filterChainDefinitionMap.put("/**", "authc"); // 其他路径均需要身份认证，一般位于最下面，优先级最低

       shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
//        shiroFilterFactoryBean.setLoginUrl("/"); //登录路径
//        shiroFilterFactoryBean.setSuccessUrl("/user/main");// 主页
        // shiroFilterFactoryBean.setUnauthorizedUrl("/error");//验证失败跳转的路径
        return shiroFilterFactoryBean;
    }

    /**
     * SecurityManager安全管理器；shiro的核心
     * 
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager(myRealm());
        return defaultWebSecurityManager;
    }

    /**
     * 自定义Realm
     * 
     * @return
     */
    @Bean
    public MyRealm myRealm() {
        MyRealm myRealm = new MyRealm();
        myRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myRealm;
    }

    /**
     * 配置加密匹配，使用MD5的方式，进行1024次加密
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(1024);
        return hashedCredentialsMatcher;
    }

    /**
     * 配置Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 自动创建代理类，若不添加，Shiro的注解可能不会生效。
     */
    @Bean
    @DependsOn({ "lifecycleBeanPostProcessor" })
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启Shiro的注解
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }


    //
    // @Bean
    // public HandlerExceptionResolver solver(){
    // HandlerExceptionResolver handlerExceptionResolver=new MyExceptionResolver();
    // return handlerExceptionResolver;
    // }
}