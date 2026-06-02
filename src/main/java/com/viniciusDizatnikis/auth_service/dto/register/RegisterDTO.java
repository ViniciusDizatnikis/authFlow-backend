package com.viniciusDizatnikis.auth_service.dto.register;

public record RegisterDTO(
        String name,
        String email,
        String password
) {}