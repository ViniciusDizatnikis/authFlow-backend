package com.viniciusDizatnikis.auth_service.dto.register;

public record RegisterResponseDTO(
        int status,
        String code,
        String message,
        String userId,
        String username,
        String email
) { }