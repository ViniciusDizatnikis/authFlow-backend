package com.viniciusDizatnikis.auth_service.dto.register;

public record VerifyEmailDTO(
        String email,
        String code
) {}