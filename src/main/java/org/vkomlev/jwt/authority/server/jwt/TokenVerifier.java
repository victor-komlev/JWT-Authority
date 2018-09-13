package org.vkomlev.jwt.authority.server.jwt;

import org.springframework.stereotype.Component;

/**
 * Created by vkomlev on 2017-05-27.
 */
@Component
public class TokenVerifier {
    public boolean verify(String jti) {
        return true;
    }
}
