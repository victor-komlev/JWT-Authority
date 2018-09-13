package org.vkomlev.jwt.authority.server.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class RawAccessJwtToken implements JwtToken {
    private static Logger logger = LoggerFactory.getLogger(RawAccessJwtToken.class);

    private String token;

    public RawAccessJwtToken(String token) {
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }

}
