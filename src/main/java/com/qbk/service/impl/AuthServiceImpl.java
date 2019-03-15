package com.qbk.service.impl;

import com.qbk.entity.SysUser;
import com.qbk.jwt.JwtTokenUtil;
import com.qbk.mapper.UserMapper;
import com.qbk.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * UsernamePasswordAuthenticationToken继承AbstractAuthenticationToken实现Authentication
 所以当在页面中输入用户名和密码之后首先会进入到UsernamePasswordAuthenticationToken验证(Authentication)，
 然后生成的Authentication会被交由AuthenticationManager来进行管理
 而AuthenticationManager管理一系列的AuthenticationProvider，
 而每一个Provider都会通UserDetailsService和UserDetail来返回一个
 以UsernamePasswordAuthenticationToken实现的带用户名和密码以及权限的Authentication
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * 身份验证管理器
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("jwtUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder ;



    @Override
    public String login(String username, String password) {
        /*  认证过程：
            1.username和password被获得后封装到一个UsernamePasswordAuthenticationToken（Authentication接口的实例）的实例中
            2.这个token被传递给AuthenticationManager进行验证
            3.成功认证后AuthenticationManager将返回一个得到完整填充的Authentication实例
            4.通过调用SecurityContextHolder.getContext().setAuthentication(...)，参数传递authentication对象，来建立安全上下文（security context）
         */
        //使用name和password封装成为的token
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        //将token传递给AuthenticationManager进行验证
        //此步骤会调用 UserDetailsService接口的loadUserByUsername(String username) 方法。 返回UserDetails接口的实现类
        //此时通过自定义查询数据库拿到加密后的密码 和 封装的token中的密码进行校验。不同则抛出异常。
        Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //根据用户名返回用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //生成令牌
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public String register(SysUser user) {
        String username = user.getUsername();
        if (userMapper.findByUsername(username) != null) {
            return "用户已存在";
        }
        //密码加密
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        List<String> roles = new ArrayList<>();
        //TODO 未添加权限
//        roles.add("ROLE_USER");
//        user.setRoles(roles);

        userMapper.insert(user);
        return "success";
    }

    @Override
    public String refreshToken(String oldToken) {
        String token = oldToken.substring("Bearer ".length());
        if (!jwtTokenUtil.isTokenExpired(token)) {
            return jwtTokenUtil.refreshToken(token);
        }
        return "error";
    }
}
