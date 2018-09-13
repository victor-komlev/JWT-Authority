package org.vkomlev.jwt.authority.server.exceptions;

/**
 * Created by vkomlev on 2017-05-28.
 */
public class CertificateUploadException extends RuntimeException {

    public CertificateUploadException() {
        super();
    }

    public CertificateUploadException(String message) {
        super(message);
    }

    public CertificateUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateUploadException(Throwable cause) {
        super(cause);
    }
}
