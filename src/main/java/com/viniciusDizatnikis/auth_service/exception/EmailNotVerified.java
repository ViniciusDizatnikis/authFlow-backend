package com.viniciusDizatnikis.auth_service.exception;

public class EmailNotVerified extends RuntimeException {

    public EmailNotVerified(String message) {
        super(message);
    }

    public EmailNotVerified(String message, Throwable cause) {
        super(message, cause);
    }

}