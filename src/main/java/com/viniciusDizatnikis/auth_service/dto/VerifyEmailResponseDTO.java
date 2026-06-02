package com.viniciusDizatnikis.auth_service.dto;

public record VerifyEmailResponseDTO(
        int status,
        String code,
        String message
) { }
