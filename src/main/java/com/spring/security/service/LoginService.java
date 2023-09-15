package com.spring.security.service;

import com.alibaba.fastjson.JSON;
import com.spring.security.domain.LoginUser;
import com.spring.security.vo.LoginBody;
import com.spring.security.jwt.JwtUtil;
import com.spring.security.redis.RedisCache;
import com.spring.security.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author lxl
 * @date 2023/9/13 14:14
 */
@Service
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;

    //认证成功的话要生成一个jwt，放入响应中返回。
    //并且为了让用户下回请求时能通过jwt识别出具体的是哪个用户，
    //我们需要把用户信息存入redis，可以把用户id作为key。
    public ResponseResult login(LoginBody loginBody) {
        // AuthenticationManager的authenticate方法进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginBody.getPrincipal(), loginBody.getPrincipal());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 如果认证没通过，给出对应提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("认证失败！");
        }
        // 如果认证通过了，使用UUID(用户ID)生成JWT
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        // 把完整的用户信息存入redis
        String uuid = JwtUtil.getUUID();
        String jwt = JwtUtil.createJWT(uuid);
        loginUser.setUuid(uuid);
        redisCache.setCacheObject("login_" + uuid, JSON.toJSONString(loginUser));
        // 把token响应给前端
        HashMap<String, String> map = new HashMap<>();
        map.put("token", jwt);
        return new ResponseResult(200, "登录成功！", map);
    }


    public ResponseResult logout() {
        // 获取SecurityContextHolder中的用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String uuid = loginUser.getUuid();
        // 删除redis中的值
        redisCache.deleteObject("login_" + uuid);
        return null;
    }

}
