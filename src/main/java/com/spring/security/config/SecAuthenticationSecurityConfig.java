package com.spring.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.security.domain.LoginUser;
import com.spring.security.filter.SecAuthFilter;
import com.spring.security.filter.SecAuthProvider;
import com.spring.security.jwt.TokenResolver;
import com.spring.security.redis.RedisCache;
import com.spring.security.response.ResponseResult;
import com.spring.security.utils.SpringUtils;
import com.spring.security.vo.CrmAuthToken;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 自定义安全适配器
 *
 * @author lxl
 * @date 2023/9/13 17:54
 */
@Configuration
public class SecAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    public static final String LOGIN_PATTERN_URL = "/user/login";

    public static final String CAPTCHA_PREFIX = "captcha:";

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SecAuthenticationSecurityConfig.class);

    @Autowired
    private TokenResolver resolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        super.configure(builder);
        //路径判断 只有匹配是这个路径就走
        AntPathRequestMatcher reqMatcher = new AntPathRequestMatcher(LOGIN_PATTERN_URL, HttpMethod.POST.name());
        //AbstractAuthenticationProcessingFilter
//        这是一个抽象类，定义了认证处理的过程。是一个模板类。
//        处理步骤
//        根据RequestMatcher，判断是否需要进行认证处理。
//        （这里每个filter实现类，都需要传入一个处理的url路径，当我们的请求match这个路径时，才会被该filter处理）
//        进行认证处理
//        sesssion处理
//        认证失败的处理
//        允许通过设置属性值continueChainBeforeSuccessfulAuthentication,直接跳过认证成功处理。
//        认证成功的处理

        //自定义 一个认证处理过程
        //实现自定义身份验证处理过程
        SecAuthFilter authFilter = new SecAuthFilter(reqMatcher);
        //设置认证管理器（（身份验证提供程序））
        authFilter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        //验证成功
        authFilter.setAuthenticationSuccessHandler((req, resp, authentication) -> {
            log.info("登录成功");
            resp.setContentType("application/json;charset=UTF-8");
            CrmAuthToken sourceToken = ((CrmAuthToken) authentication).getSourceToken();
            SpringUtils.getBean(RedisCache.class).deleteObject(CAPTCHA_PREFIX + sourceToken.getRequestId());
            // 生成令牌
            String token = resolver.createToken((LoginUser) authentication.getPrincipal());
//            LogFactory.loginSuccess(sourceToken.getMode(), SecurityUtils.getFullName());
            String respText = objectMapper.writeValueAsString(new ResponseResult(200, "登录成功", token));
            resp.getWriter().write(respText);
        });
        //验证成功失败
        authFilter.setAuthenticationFailureHandler((req, resp, e) -> {
            log.info("登陆失败");
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(objectMapper.writeValueAsString(new ResponseResult(500, e.getMessage())));
        });

        //自定义 认证管理方式（身份验证提供程序）
        //证是由 AuthenticationManager 来管理的，
        //但是真正进行认证的是 AuthenticationManager 中定义的 AuthenticationProvider
        //实现自定义身份验证逻辑
        SecAuthProvider provider = new SecAuthProvider();
        // 如果没有指定对应关联的 AuthenticationProvider 对象，
        // Spring Security 默认会使用 DaoAuthenticationProvider。
        builder.authenticationProvider(provider)
                .addFilterAfter(authFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
