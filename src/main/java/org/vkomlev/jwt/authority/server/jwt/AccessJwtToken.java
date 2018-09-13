package org.vkomlev.jwt.authority.server.jwt;

import io.jsonwebtoken.Claims;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class AccessJwtToken implements JwtToken {
    private final String rawToken;
    private Claims claims;

    protected AccessJwtToken(final String token, Claims claims) {
        this.rawToken = token;
        this.claims = claims;
    }

    public String getToken() {
        return this.rawToken;
    }

    public Claims getClaims() {
        return claims;
    }
}
