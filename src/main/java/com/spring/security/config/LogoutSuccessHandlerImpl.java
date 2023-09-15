package com.spring.security.config;

import com.alibaba.fastjson.JSON;
import com.spring.security.domain.LoginUser;
import com.spring.security.jwt.TokenResolver;
import com.spring.security.response.ResponseResult;
import com.spring.security.utils.WebUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 退出登录处理程序
 *
 * @author lxl
 * @date 2023/9/15 9:47
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(LogoutSuccessHandlerImpl.class);


    @Autowired
    private TokenResolver tokenResolver;


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LoginUser loginUser = tokenResolver.getLoginUser(request);
        if (!Objects.isNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 记录用户退出日志
//            LogFactory.logOutSuccess(loginUser.getUser().getFullName(), userName, "退出成功", true);
            // 删除用户缓存记录
            tokenResolver.delLoginUser(loginUser.getToken());
        }
        ResponseResult result = new ResponseResult(HttpStatus.OK.value(), "退出成功");
        WebUtils.renderString(response, JSON.toJSONString(result));
        log.info("退出成功");
    }

}
