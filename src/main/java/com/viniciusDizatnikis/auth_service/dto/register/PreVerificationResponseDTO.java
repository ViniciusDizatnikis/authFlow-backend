package com.viniciusDizatnikis.auth_service.dto.register;

public record PreVerificationResponseDTO(
        int status,
        String code,
        String message,
        String email,
        boolean verified
) { }