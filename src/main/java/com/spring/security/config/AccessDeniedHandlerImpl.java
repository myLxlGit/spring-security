package com.spring.security.config;

import com.alibaba.fastjson.JSON;
import com.spring.security.response.ResponseResult;
import com.spring.security.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 授权失败处理器
 *
 * @author lxl
 * @date 2023/9/13 14:33
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 处理异常
        ResponseResult result = new ResponseResult(HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage());
        String json = JSON.toJSONString(result);
        WebUtils.renderString(response, json);
    }

}
