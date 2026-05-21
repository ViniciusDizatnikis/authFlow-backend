package com.viniciusDizatnikis.auth_service.repositories;

import com.viniciusDizatnikis.auth_service.domain.token.PendingEmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PendingEmailTokenRepository extends JpaRepository<PendingEmailToken, String> {

    Optional<PendingEmailToken> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    // busca token ja verificado para liberar o cadastro
    Optional<PendingEmailToken> findTopByEmailAndVerifiedTrueAndUsedFalseOrderByCreatedAtDesc(String email);
}