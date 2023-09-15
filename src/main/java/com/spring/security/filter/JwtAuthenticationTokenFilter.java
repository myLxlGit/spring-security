package com.spring.security.filter;

import com.spring.security.domain.LoginUser;
import com.spring.security.jwt.TokenResolver;
import com.spring.security.redis.RedisCache;
import com.spring.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 认证过滤器
 *
 * @author lxl
 * @date 2023/9/13 14:18
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    private final TokenResolver resolver;

    public JwtAuthenticationTokenFilter(TokenResolver resolver) {
        this.resolver = resolver;
    }

    //我们需要自定义一个过滤器，这个过滤器会去获取请求头中的token，对token进行解析取出其中的userid。
    //使用userid去redis中获取对应的LoginUser对象。
    //然后封装Authentication对象存入SecurityContextHolder
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        LoginUser loginUser = resolver.getLoginUser(request);

        if (!Objects.isNull(loginUser)
                && Objects.isNull(SecurityUtils.getAuthentication())) {
            resolver.verifyToken(loginUser);

            // 获取权限信息封装到Authentication中
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            // 存入SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 放行
        filterChain.doFilter(request, response);

//        // 获取token
//        String token = request.getHeader("token");
//        // token是空则放行
//        if (!StringUtils.hasText(token)) {
//            //放行
//            filterChain.doFilter(request, response);
//            return;
//        }
//        // 解析token
//        String uuid;
//        try {
//            Claims claims = JwtUtil.parseJWT(token);
//            uuid = claims.getSubject();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("token非法！！！");
//        }
//        // 从redis中获取用户信息
//        String redisKey = "login_" + uuid;
//        LoginUser loginUser = JSON.parseObject(redisCache.getCacheObject(redisKey), LoginUser.class);
//        if (Objects.isNull(loginUser)) {
//            throw new RuntimeException("用户未登录！！！");
//        }
//        // 获取权限信息封装到Authentication中
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
//        // 存入SecurityContextHolder
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行
//        filterChain.doFilter(request, response);
    }

}
