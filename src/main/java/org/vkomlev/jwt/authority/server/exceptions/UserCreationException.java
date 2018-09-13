package org.vkomlev.jwt.authority.server.exceptions;

/**
 * Created by vkomlev on 2017-05-28.
 */
public class UserCreationException extends RuntimeException {
    public UserCreationException() {
    }

    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserCreationException(Throwable cause) {
        super(cause);
    }
}
