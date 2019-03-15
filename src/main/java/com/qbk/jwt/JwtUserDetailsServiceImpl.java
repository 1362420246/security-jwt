package com.qbk.jwt;

import com.qbk.entity.SysUser;
import com.qbk.jwt.JwtUser;
import com.qbk.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserDetailsService 接口实现类
 *
 * 处理用户信息获取逻辑： import org.springframework.security.core.userdetails.UserDetailsService;

 自定义类实现UserDetailsService接口重写：
 UserDetails loadUserByUsername(String username)throws UsernameNotFoundException ；根据用户名加载用户信息

 返回：
 org.springframework.security.core.userdetails.User  implements UserDetails
 构造函数1：
 return  new User(username,"123456", AuthorityUtils.commaSeparatedStringToAuthorityList("admin"))
 参数：用户名，密码，权限

 构造函数2:
 return new User(username,"123456", true,true,true,true,
 AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
 参数：用户名，密码，是否可用，是否账户不过期 ，是否凭证不过期，是否非锁定账户，权限

 也可以返回 自己实现 UserDetails接口的实现类
 */
@Service("jwtUserDetailsServiceImpl")
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名加载用户信息
     * @param username 用户名称
     * @return UserDetails接口实现类
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户名称
        SysUser user = userMapper.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            UserDetails jwtUser = new JwtUser(user.getUsername(), user.getPassword(),
                    user.getRoles().stream().map(authority -> new SimpleGrantedAuthority(authority.getName()))
                            .collect(Collectors.toList()));
            return jwtUser;
        }
    }
}

