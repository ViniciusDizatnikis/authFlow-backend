package com.viniciusDizatnikis.auth_service.dto;

public record ResetPasswordDTO(
        String email,
        String code,
        String newPassword
) {}