package org.vkomlev.jwt.authority.server.exceptions;

import org.vkomlev.jwt.authority.server.jwt.JwtToken;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class JwtValidationException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;

    private JwtToken token;

    public JwtValidationException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
