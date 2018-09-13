package org.vkomlev.jwt.authority.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class User {
    private Long id;

    private String username;

    private String password;

    private List<GrantedAuthority> authorities;

    private String tenantName;

    @JsonIgnore
    private LoginRequestDTO loginRequest;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantName() {
        return tenantName;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", password='" + password + '\''
                + ", authorities=" + authorities + ", tenantName='" + tenantName + '\'' + '}';
    }

    public void setLoginRequest(LoginRequestDTO loginRequest) {
        this.loginRequest = loginRequest;
    }

    @JsonIgnore
    public LoginRequestDTO getLoginRequest() {
        return loginRequest;
    }
}
