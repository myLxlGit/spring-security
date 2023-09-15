package com.spring.security.service.impl;

import com.spring.security.domain.LoginUser;
import com.spring.security.utils.PassWordUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 认证
 *
 * @author lxl
 * @date 2023/9/13 14:05
 */
@Service
public class AcUserDetailsServiceImpl implements IAuthService {

    //创建一个类实现UserDetailsService接口，重写其中的方法。
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息（查询数据库，这里使用假数据）
        if (!"lxl".equals(username)) {
            throw new RuntimeException("用户名错误！！！");
        }
        //注意：如果你想让用户的密码是明文存储，需要在密码前加 {noop}。
        String password = "$2a$10$1H/vUakw7ud3C7iXpAulNOYwKAcAVXWQQKoVKcL991FbcAjHd/.Ci";
        String userId = "1";
        //查询对应的权限信息 （查询数据库，这里使用假数据）
        List<String> list = new ArrayList<>(Arrays.asList("test", "admin"));

        //做一些用的数据校验

        // 把数据封装成LoginUser对象返回
        return new LoginUser(userId, username, password, list);
    }

    @Override
    public void afterCall(UserDetails userDetails, Authentication authentication) {
        String dbPwd = userDetails.getPassword();
        if (!PassWordUtils.matchesPassword((String) authentication.getCredentials(), dbPwd)) {
            throw new BadCredentialsException("用户名密码错误");
        }
    }

}
