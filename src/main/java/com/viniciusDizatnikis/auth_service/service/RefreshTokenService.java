package com.viniciusDizatnikis.auth_service.service;

import com.viniciusDizatnikis.auth_service.domain.token.RefreshToken;
import com.viniciusDizatnikis.auth_service.domain.user.User;
import com.viniciusDizatnikis.auth_service.infra.security.TokenService;
import com.viniciusDizatnikis.auth_service.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

    // cria e salva novo refresh token pro usuario
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        return refreshTokenRepository.save(token);
    }

    // valida e retorna novo access token
    public String refresh(String refreshTokenValue) {
        RefreshToken token = refreshTokenRepository
                .findByTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token invalido ou revogado"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            throw new RuntimeException("Refresh token expirado. Faca login novamente");
        }

        // gera novo access token
        return tokenService.generateAccessToken(token.getUser());
    }

    // logout — revoga o refresh token
    public void revoke(String refreshTokenValue) {
        RefreshToken token = refreshTokenRepository
                .findByTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Token nao encontrado"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    // logout geral — revoga todos os tokens do usuario
    public void revokeAll(String userId) {
        refreshTokenRepository.deleteAllByUser_Id(userId);
    }
}