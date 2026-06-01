package com.viniciusDizatnikis.auth_service.dto.register;

import java.time.LocalDateTime;

public record PreRegisterResponseDTO(
        int status,
        String code,
        String message,
        String email,
        LocalDateTime expiresAt
) {
}