package com.spring.security.vo;

import lombok.Data;

/**
 * @author lxl
 * @date 2023/9/13 14:03
 */
@Data
public class LoginBody {

    //登录身份标记（存放用户的身份标识信息，比如用户名）
    private String principal;

    //登录身份凭证（存放用户的验证凭证比如密码）
    private String credentials;

    /**
     * 登录方式
     * @see com.spring.security.vo.LoginMode
     */
    private String mode;

}
