package org.vkomlev.jwt.authority.server.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    private static final long serialVersionUID = 3705043083010304496L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}
