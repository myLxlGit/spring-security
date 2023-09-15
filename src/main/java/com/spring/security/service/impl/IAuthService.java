package com.spring.security.service.impl;

import org.slf4j.Logger;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author lxl
 * @date 2023/9/13 17:20
 */
public interface IAuthService extends UserDetailsService {


//    default void checkUserStatus(UserCachedVO cachedVO, Logger log, String username) {
//        if (StringUtils.isNull(cachedVO)) {
//            log.info("登录用户：{} 不存在.", username);
//            throw new UsernameNotFoundException("用户名密码错误");
//        } else if (UserStatus.DELETED.getCode().equals(cachedVO.getDelFlag())) {
//            log.info("登录用户：{} 已被删除.", username);
//            throw new AccountExpiredException("对不起，您的账号：" + username + " 已被删除");
//        } else if (UserStatus.DISABLE.getCode().equals(cachedVO.getUserStatus())) {
//            log.info("登录用户：{} 已被停用.", username);
//            throw new LockedException("对不起，您的账号：" + username + " 已停用");
//        }
//    }

    default void afterCall(UserDetails userDetails, Authentication authentication) {}




}
