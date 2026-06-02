package com.viniciusDizatnikis.auth_service.exception;

public class EmailAlreadyRegistered  extends RuntimeException {

    public EmailAlreadyRegistered(String message) {
        super(message);
    }

    public EmailAlreadyRegistered(String message, Throwable cause) {
        super(message, cause);
    }

}