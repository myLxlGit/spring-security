package com.spring.security.filter;

import com.alibaba.fastjson.JSON;
import com.spring.security.vo.CrmAuthToken;
import com.spring.security.vo.LoginBody;
import com.spring.security.vo.LoginMode;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 自定义认证处理的过程
 * <p>
 * 参考（UsernamePasswordAuthenticationFilter）
 *
 * @author lxl
 * @date 2023/9/13 18:08
 */
public class SecAuthFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SecAuthFilter.class);

    public SecAuthFilter(RequestMatcher reqMat) {
        super(reqMat);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException, IOException, ServletException {
        if (!HttpMethod.POST.name().equalsIgnoreCase(req.getMethod())) {
            throw new AuthenticationServiceException("不支持的请求方式");
        }
        //解析登录接口获取的登录信息
        ServletInputStream inputStream = req.getInputStream();
        String bodyContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        ;
        LoginBody loginBody = JSON.parseObject(bodyContent, LoginBody.class);

        String userName = loginBody.getPrincipal();
        String pwd = loginBody.getCredentials();
        String mode = loginBody.getMode();

        //自定义接受 身份验证数据的封装都继承 AbstractAuthenticationToken
        //Authentication主要职责就是封装身份验证时候需要的信息数据
        //身份信息交互的纽带：Authentication
        CrmAuthToken authToken = new CrmAuthToken(userName, pwd);
        authToken.setMode(LoginMode.valueOf(mode));
        //details则存放除了用户名和密码其他可能会被用于身份验证的信息，比如应用限定用户的使用ip范围场景下，
        //ip信息可能便会被存放在details做辅助的验证信息使用
        authToken.setDetails(this.authenticationDetailsSource.buildDetails(req));

        // AuthenticationManager的authenticate方法进行用户认证
        //验证身份的入口方法
        //需要将生成的验证令牌传进去，一系列验证之后，
        //如果验证成功会把令牌中的Authenticated属性设置为true，前面我们说过，它被设置为false。
        return super.getAuthenticationManager().authenticate(authToken);
    }


}
