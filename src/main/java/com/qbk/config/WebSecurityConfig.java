package com.qbk.config;


import com.qbk.exception.EntryPointUnauthorizedHandler;
import com.qbk.exception.RestAccessDeniedHandler;
import com.qbk.jwt.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全模块配置
 *WebSecurityConfig该类是 Spring Security 的配置类，该类的三个注解分别是标识该类是配置类、开启 Security 服务、开启全局 Securtiy 注解。
 首先将我们自定义的 userDetailsService 注入进来，在 configure() 方法中使用 auth.userDetailsService() 方法替换掉默认的 userDetailsService。
 这里我们还指定了密码的加密方式（5.0 版本强制要求设置），因为我们数据库是明文存储的，所以明文返回即可，如下所示：
 */
@Configuration
//开启 Security 服务
@EnableWebSecurity
//开启启全局 Securtiy 注解
// Spring Security默认是禁用注解的，要想开启注解， 需要在继承WebSecurityConfigurerAdapter的类上加@EnableGlobalMethodSecurity注解， 来判断用户对某个控制层的方法是否具有访问权限
@EnableGlobalMethodSecurity(prePostEnabled = true)
/**
 * Security配置：
 实现 适配器：
 WebSecurityConfigurerAdapter
 重写方法：
 protected void configure(HttpSecurity http) throws Exception
 public void configure(WebSecurity web) throws Exception
 */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("jwtUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    /**
     * jwt过滤器
     */
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     *  用来解决匿名用户访问无权限资源时的异常
     */
    @Autowired
    private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;

    /**
     *  用来解决认证过的用户访问无权限资源时的异常
     */
    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;

    /**
     * 装载BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     *  配置 AuthenticationManagerBuilder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 设置UserDetailsService
        // 使用BCrypt进行密码的hash
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     *
     Spring Security下的枚举SessionCreationPolicy,管理session的创建策略
     ALWAYS: 总是创建HttpSession
     IF_REQUIRED : Spring Security只会在需要时创建一个HttpSession
     NEVER : Spring Security不会创建HttpSession，但如果它已经存在，将可以使用HttpSession
     STATELESS : Spring Security永远不会创建HttpSession，它不会使用HttpSession来获取SecurityContext

     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable().
                // 基于token，所以不需要session
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                // 允许对于网站静态资源的无授权访问
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/user/**").permitAll()
                // Swagger2 权限放行
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/login.html").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                // 禁用缓存
                .and().headers().cacheControl();

        //TODO
        //httpSecurity.authorizeRequests().antMatchers("").hasRole("");

        //添加自定义过滤器
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //配置自定义异常
        httpSecurity.exceptionHandling().authenticationEntryPoint(entryPointUnauthorizedHandler).accessDeniedHandler(restAccessDeniedHandler);

    }

}