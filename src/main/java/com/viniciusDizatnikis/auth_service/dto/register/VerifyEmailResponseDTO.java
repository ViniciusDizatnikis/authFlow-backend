package com.viniciusDizatnikis.auth_service.dto.register;

public record VerifyEmailResponseDTO(
        int status,
        String code,
        String message
) { }
