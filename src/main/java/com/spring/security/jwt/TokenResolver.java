package com.spring.security.jwt;


import com.spring.security.domain.LoginUser;
import com.spring.security.redis.RedisCache;
import com.spring.security.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qixlin
 * @date 2021/06/23 13:03
 */
@Service
public class TokenResolver {

    private static final Logger log = LoggerFactory.getLogger(TokenResolver.class);

    /**
     * 令牌自定义标识
     */
    private static final String HEADER = "Authorization";

    /**
     * 令牌秘钥
     */
    private static final String SECRET = "asdfghjklzxcvbnm";

    /**
     * 令牌有效期
     */
    private static final int EXPIRE_TIME = 1440;

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private RedisCache cache;

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 获取用户身份信息
     *
     * @param request Http request
     * @return LoginUser
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        return getLoginUser(token, request);
    }


    public LoginUser getLoginUser(String token, HttpServletRequest request) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
            String uuid = (String) claims.get(LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            return cache.getCacheObject(userKey);
        } catch (Throwable e) {
            log.debug("无法处理的请求认证头：path：{}, token: {}", request.getServletPath(), token);
            return null;
        }
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser loginUser
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= TimeUnit.MINUTES.toMillis(120)) {
            refreshToken(loginUser);
        }
//        if (loginUser.getPlatform() == Platform.APP && expireTime - currentTime <= TimeUnit.MINUTES.toMillis(43200)) {
//            refreshToken(loginUser);
//        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (!StringUtils.hasText(token)) {
            String userKey = getTokenKey(token);
            cache.deleteObject(userKey);
        }
    }

    public String createToken(LoginUser loginUser) {
        String token = loginUser.getUserId() + ":" + JwtUtil.getUUID();

        loginUser.setToken(token);
        refreshToken(loginUser);
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(LOGIN_USER_KEY, token);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());

        int expTime = EXPIRE_TIME;
//        if (loginUser.getPlatform() == Platform.WEB) {
//            expTime = expireTime;
//        }
//        if (loginUser.getPlatform() == Platform.APP) {
//            expTime = appExpireTime;
//        }
        loginUser.setExpireTime(loginUser.getLoginTime() + expTime * TimeUnit.MINUTES.toMillis(1));
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        cache.setCacheObject(userKey, loginUser, expTime, TimeUnit.MINUTES);
//        publisher.publishEvent(new LoginEvent(this, userKey, loginUser.getUser().getJobNo()));
    }

    public void refreshToken() {
        refreshToken(SecurityUtils.getLoginUser());
    }

    /**
     * 获取请求token
     *
     * @param request request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER);
        if (!StringUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    private String getTokenKey(String uuid) {
        return LOGIN_TOKEN_KEY + uuid;
    }

    /**
     * 刷新缓存
     */
//    public void refreshCache() {
//        LoginUser loginUser = SecurityUtils.getLoginUser();
//        try {
//            LoginUser newLoginUser = (LoginUser) authService.loadUserByUsername(loginUser.getUser().getUserTel());
//            loginUser.setUser(newLoginUser.getUser());
//            List<CompatibleMenuDTO> me = compatibleUserService.selectMenu(loginUser.getUser().getUserId());
//            loginUser.setApiKey(sysApiMenuService.getApiPermKeyList(me.stream().map(CompatibleMenuDTO::getMenuId).collect(Collectors.toList())));
//            refreshToken(loginUser);
//        } catch (Exception e) {
//            log.error("{}重置用户缓存失败", loginUser.getUser().getFullName());
//        }
//    }

    public void deleteCache(String userId) {
        Collection<String> keys = cache.keys(getTokenKey(userId));
        cache.deleteObject(keys);
    }

}
