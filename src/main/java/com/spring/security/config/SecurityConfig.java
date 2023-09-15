package com.spring.security.config;

import com.spring.security.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author lxl
 * @date 2023/9/13 14:09
 */
@Configuration
//在security配置类中添加如下注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private SecAuthenticationSecurityConfig secAuthenticationSecurityConfig;

    //认证过滤器
    //我们需要自定义一个过滤器，这个过滤器会去获取请求头中的token，对token进行解析取出其中的userid。
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    //认证失败处理器
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    //授权失败处理器
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * 退出处理类
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

//    实际项目中我们不会把密码明文存储在数据库中。
//
//    默认使用的PasswordEncoder要求数据库中的密码格式为：{id}password 。它会根据id去判断密码的加密方式。但是我们一般不会采用这种方式。所以就需要替换PasswordEncoder。
//
//    我们一般使用SpringSecurity为我们提供的BCryptPasswordEncoder。
//
//    我们只需要使用把BCryptPasswordEncoder对象注入Spring容器中，SpringSecurity就会使用该PasswordEncoder来进行密码校验

    //旧版本：我们可以定义一个SpringSecurity的配置类，
    //SpringSecurity要求这个配置类要继承WebSecurityConfigurerAdapter。

//    @Configuration
//    public class SecurityConfig extends WebSecurityConfigurerAdapter {
//        @Bean
//        public PasswordEncoder passwordEncoder(){
//            return new BCryptPasswordEncoder();
//        }
//        @Bean
//        @Override
//        public AuthenticationManager authenticationManagerBean() throws Exception {
//            return super.authenticationManagerBean();
//        }
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http
//                    //关闭csrf
//                    .csrf().disable()
//                    //不通过Session获取SecurityContext
//                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                    .and()
//                    .authorizeRequests()
//                    // 对于登录接口 允许匿名访问
//                    .antMatchers("/user/login").anonymous()
//                    // 除上面外的所有请求全部需要鉴权认证
//                    .anyRequest().authenticated();
//        }
//    }

    //新版本：SpringSecurity要求这个配置类不要继承WebSecurityConfigurerAdapter。
    // 创建BCryptPasswordEncoder注入容器
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //接下我们需要自定义登陆接口，
    //然后让SpringSecurity对这个接口放行，让用户访问这个接口的时候不用登录也能访问。

    //在接口中我们通过AuthenticationManager的authenticate方法来进行用户认证，
    //所以需要在SecurityConfig配置中把AuthenticationManager注入容器。
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //还要在配置类中放行直接访问登录的请求
    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CSRF是指跨站请求伪造（Cross-site request forgery），是web常见的攻击之一。
                // CSRF禁用，因为不使用session
                .csrf().disable()
                //应用子容器处理
                .apply(secAuthenticationSecurityConfig).and()
                // 不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 过滤请求
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/login").anonymous()

                /*------------------外部接口------------------*/
                .antMatchers("/erp/api/**").permitAll()
                /*------------------外部接口------------------*/

                //白名单
                .antMatchers("/user/hello1").permitAll()
                //可扩展

                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        //退出
        httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);

        // 把token校验过滤器添加到过滤器链中
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置异常处理器
        httpSecurity.exceptionHandling()
                // 配置认证失败处理器
                .authenticationEntryPoint(authenticationEntryPoint)
                // 配置授权失败处理器
                .accessDeniedHandler(accessDeniedHandler);

        // 允许跨域
        httpSecurity.cors();

        return httpSecurity.build();
    }




}
