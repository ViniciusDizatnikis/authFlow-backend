package com.viniciusDizatnikis.auth_service.dto;

public record UserResponseDTO(
        String id,
        String name,
        String email,
        Boolean emailVerified
) {}