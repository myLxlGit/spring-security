package com.spring.security.vo;

import com.spring.security.service.impl.IAuthService;
import com.spring.security.service.impl.AcUserDetailsServiceImpl;

/**
 * 登录方式
 *
 * @author qixlin
 * @date 2021/06/23 10:58
 */
public enum LoginMode {

    /**
     * 账户名登录
     */
    AC(false, AcUserDetailsServiceImpl.class);
//    /**
//     * 手机号登录
//     */
//    PH(true, PhAuthServiceImpl.class),
//    /**
//     * 微信
//     */
//    WC(false, WcAuthServiceImpl.class),
//    /**
//     *
//     */
//    SASL(false, SaslAuthServiceImpl.class);

    private final boolean needCode;

    private final Class<? extends IAuthService> serviceClass;

    LoginMode(boolean needCode, Class<? extends IAuthService> serviceClass) {
        this.needCode = needCode;
        this.serviceClass = serviceClass;
    }

    public Class<? extends IAuthService> getServiceClass() {
        return serviceClass;
    }

    public boolean isNeedCode() {
        return needCode;
    }
}
