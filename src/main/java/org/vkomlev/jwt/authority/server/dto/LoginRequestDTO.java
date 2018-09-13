package org.vkomlev.jwt.authority.server.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class LoginRequestDTO {
    private String username;
    private String password;
    private final String requiredSub;
    private final String requiredIss;
    private final Long requiredIat;
    private final Long requiredExp;
    private final Long requiredNbf;
    private final String requiredJti;

    private List<String> excludeClaims;

    @JsonCreator
    public LoginRequestDTO(@JsonProperty("username") String username, @JsonProperty("password") String password,
            @JsonProperty("sub") String requiredSub, @JsonProperty("iss") String requiredIss,
            @JsonProperty("iat") Long requiredIat, @JsonProperty("exp") Long requiredExp,
            @JsonProperty("nbf") Long requiredNbf, @JsonProperty("jti") String requiredJti,
            @JsonProperty("excludeClaims") List<String> excludeClaims) {
        this.username = username;
        this.password = password;
        this.requiredSub = requiredSub;
        this.requiredIss = requiredIss;
        this.requiredIat = requiredIat;
        this.requiredExp = requiredExp;
        this.requiredNbf = requiredNbf;
        this.requiredJti = requiredJti;
        this.excludeClaims = excludeClaims;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRequiredSub() {
        return requiredSub;
    }

    public String getRequiredIss() {
        return requiredIss;
    }

    public Long getRequiredIat() {
        return requiredIat;
    }

    public Long getRequiredExp() {
        return requiredExp;
    }

    public Long getRequiredNbf() {
        return requiredNbf;
    }

    public String getRequiredJti() {
        return requiredJti;
    }

    public List<String> getExcludeClaims() {
        return excludeClaims;
    }

}
