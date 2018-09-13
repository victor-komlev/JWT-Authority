package org.vkomlev.jwt.authority.server.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by vkomlev on 2017-05-27.
 */
public enum ErrorCode {
    GLOBAL(2),
    AUTHENTICATION(10),
    JWT_TOKEN_EXPIRED(11);

    private int errorCode;

    ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}
