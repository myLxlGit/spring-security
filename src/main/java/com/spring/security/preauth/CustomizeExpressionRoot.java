package com.spring.security.preauth;

import com.spring.security.domain.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义权限校验方法
 *
 * @author lxl
 * @date 2023/9/13 14:41
 */
@Component("cxr")
public class CustomizeExpressionRoot {

    //我们也可以定义自己的权限校验方法，在@PreAuthorize注解中使用我们的方法。

    /**
     * 是否有的权限
     *
     * @param authority
     * @return
     */
    public boolean hasAuthority(String authority) {
        // 获取当前用户权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        List<String> permission = loginUser.getPermission();
        // 判断用户权限集合中是否存在authority
        return permission.contains(authority);
    }
}
