package com.viniciusDizatnikis.auth_service.dto;

public record VerifyEmailDTO(
        String email,
        String code
) {}