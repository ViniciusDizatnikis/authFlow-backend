package com.viniciusDizatnikis.auth_service.repositories;

import com.viniciusDizatnikis.auth_service.domain.token.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findTopByUser_EmailAndUsedFalseOrderByCreatedAtDesc(String email);
}