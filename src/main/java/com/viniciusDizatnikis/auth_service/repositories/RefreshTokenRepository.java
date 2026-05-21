package com.viniciusDizatnikis.auth_service.repositories;

import com.viniciusDizatnikis.auth_service.domain.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    // revoga todos os tokens de um usuario (logout geral)
    void deleteAllByUser_Id(String userId);
}