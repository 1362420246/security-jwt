package com.qbk.jwt.filter;

import com.qbk.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  jwt过滤器
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //获取请求头中得 参数
        String authHeader = request.getHeader("Authorization");
        //token前缀
        String tokenHead = "Bearer ";
        //判断参数是否为空 、参数是否合法开头
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            //从参数中截取 获得token
            String authToken = authHeader.substring(tokenHead.length());
            //token对应得用户名
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            //用户名是否存在
            //获取当前登录用户：SecurityContextHolder.getContext().getAuthentication()
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //通过用户名获取用户信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                //验证令牌
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        //放行
        chain.doFilter(request, response);
    }

}
