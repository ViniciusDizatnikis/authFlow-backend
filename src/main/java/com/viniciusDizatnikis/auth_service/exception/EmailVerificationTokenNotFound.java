package com.viniciusDizatnikis.auth_service.exception;

public class EmailVerificationTokenNotFound extends RuntimeException {

    public EmailVerificationTokenNotFound() {
        super("Nenhum token de verificação encontrado para este e-mail.");
    }

    public EmailVerificationTokenNotFound(String message) {
        super(message);
    }
}
