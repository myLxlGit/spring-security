package com.spring.security.vo;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 自定义身份验证数据的封装都继承 AbstractAuthenticationToken
 *
 * 参考（UsernamePasswordAuthenticationToken）
 *
 * @author qixlin
 * @date 2021/06/23 11:15
 */
public class CrmAuthToken extends AbstractAuthenticationToken {

    private final Object principal;

    private final Object credentials;

    private LoginMode mode;

    private String requestId;

    private CrmAuthToken sourceToken;

    /**
     * 创建一个有鉴权的token
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public CrmAuthToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
    }


    /**
     * 创建一个没有鉴权的token
     *
     * @param principal   principal
     * @param credentials credentials
     */
    public CrmAuthToken(Object principal, Object credentials) {
        super(null);
        this.credentials = credentials;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public LoginMode getMode() {
        return mode;
    }

    public void setMode(LoginMode mode) {
        this.mode = mode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public CrmAuthToken getSourceToken() {
        return sourceToken;
    }

    public void setSourceToken(CrmAuthToken sourceToken) {
        this.sourceToken = sourceToken;
    }

    /**
     * 复制当前对象，主要为了达到修改 principal 的目的
     * @return 新的CrmAuthToken对象
     */
//    public CrmAuthToken copy(Object principal) {
//        CrmAuthToken crmAuthToken = new CrmAuthToken(principal, getCredentials());
//        crmAuthToken.setMode(LoginMode.PH);
//        crmAuthToken.setRequestId(getRequestId());
//        crmAuthToken.setSourceToken(getSourceToken());
//        crmAuthToken.setDetails(getDetails());
//        return crmAuthToken;
//    }
}
