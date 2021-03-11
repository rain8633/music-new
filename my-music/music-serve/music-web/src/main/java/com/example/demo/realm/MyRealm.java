package com.example.demo.realm;


import com.example.demo.dao.ConsumerMapper;

import com.example.demo.model.Consumer;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class MyRealm extends AuthorizingRealm {

    @Autowired
    private ConsumerMapper consumerMapper;
//    @Autowired
//    private IRoleRespository iRoleRespository;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Consumer userEntity= (Consumer) principalCollection.getPrimaryPrincipal();
        Consumer user= consumerMapper.findByEmail(userEntity.getEmail());
        if (user==null){
            return null;
        }
        SimpleAuthorizationInfo authorizationInfo=new SimpleAuthorizationInfo();

        return authorizationInfo;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String  username= (String) authenticationToken.getPrincipal(); //获取登录用户账号

        UsernamePasswordToken token=(UsernamePasswordToken) authenticationToken;
        Consumer user = consumerMapper.findByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在!");
        }
//        if(user.getEmailstatus() ==0){
//            throw  new AccountException("邮箱未激活");
//        }
//        String password=user.getPassword();
//        if(!password.equals(new String((char[]) token.getCredentials()))){
//            throw  new AccountException("用户名或密码不正确!");
//        }
        String realmName = getName();//当前realm对象的唯一名字，调用父类的getName()方法
       ByteSource credentialsSalt = ByteSource.Util.bytes(user.getSalt()); //加密的盐值

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), credentialsSalt, realmName);
        return info;
    }

}


