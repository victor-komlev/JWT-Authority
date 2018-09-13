package org.vkomlev.jwt.authority.server.dto;

import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class ErrorResponseDTO {
    private final HttpStatus status;

    // General Error message
    private final String message;

    // Error code
    private final ErrorCode errorCode;

    private final Date timestamp;

    private final String error;

    protected ErrorResponseDTO(final String message, final ErrorCode errorCode, HttpStatus status, String error) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = new java.util.Date();
        this.error = error;
    }

    public static ErrorResponseDTO of(final String message, final ErrorCode errorCode, HttpStatus status,
            String error) {
        return new ErrorResponseDTO(message, errorCode, status, error);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }
}
