
package com.viniciusDizatnikis.auth_service.dto;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String name
) {}