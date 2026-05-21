package com.viniciusDizatnikis.auth_service.repositories;

import com.viniciusDizatnikis.auth_service.domain.token.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {

    Optional<EmailVerificationToken> findTopByUser_EmailAndUsedFalseOrderByCreatedAtDesc(String email);
}