package com.spring.security.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 封装登录用的信息
 *
 * @author lxl
 * @date 2023/9/13 14:07
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private String token;

    private String uuid;

    private String userId;

    private String username;

    private String password;

    private Long loginTime;

    private Long expireTime;

    // 存储权限信息
    private List<String> permission;

    // 存储SpringSecurity所需要的权限信息的集合
    @JSONField(serialize = false)
    // 由于redis的原因,该类型不能序列化
    private Set<GrantedAuthority> authorities;

    public LoginUser(String userId, String username, String password, List<String> permission) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.permission = permission;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (Objects.nonNull(authorities)) {
            return authorities;
        }
        // 把permissions中字符串类型的权限信息转换成GrantedAuthority对象存入authorities中
        authorities = permission.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        return authorities;
    }

    @Getter
    static class SimpleGrantedAuthority implements GrantedAuthority {

        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        private String role;

        public SimpleGrantedAuthority(String role) {
            Assert.hasText(role, "A granted authority textual representation is required");
            this.role = role;
        }

        public SimpleGrantedAuthority() {
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String getAuthority() {
            return "ROLE_" + this.role;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof SimpleGrantedAuthority) {
                return this.role.equals(((SimpleGrantedAuthority) obj).role);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.role.hashCode();
        }

        @Override
        public String toString() {
            return this.role;
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
