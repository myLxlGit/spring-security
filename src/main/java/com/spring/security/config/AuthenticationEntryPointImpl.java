package com.spring.security.config;

import com.alibaba.fastjson.JSON;
import com.spring.security.response.ResponseResult;
import com.spring.security.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败处理器
 *
 * @author lxl
 * @date 2023/9/13 14:35
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 处理异常
        ResponseResult result = new ResponseResult(HttpStatus.UNAUTHORIZED.value(), "请求访问：" + request.getRequestURI() + "认证失败，无法访问系统资源" );
        WebUtils.renderString(response, JSON.toJSONString(result));
    }

}
