package com.viniciusDizatnikis.auth_service.service;

import com.viniciusDizatnikis.auth_service.domain.token.PasswordResetToken;
import com.viniciusDizatnikis.auth_service.domain.user.User;
import com.viniciusDizatnikis.auth_service.infra.security.CodeService;
import com.viniciusDizatnikis.auth_service.repositories.PasswordResetTokenRepository;
import com.viniciusDizatnikis.auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final CodeService codeService;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    // passo 1 — usuario pede o codigo
    public String requestCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email nao encontrado"));

        String code = codeService.generateCode();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setCodeHash(codeService.hashCode(code));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(token);

        emailService.sendPasswordResetCode(email, code); // envia de verdade

        return "Codigo enviado para o email";
    }

    // passo 2 — usuario digita o codigo e a nova senha
    public void resetPassword(String email, String inputCode, String newPassword) {
        PasswordResetToken token = tokenRepository
                .findTopByUser_EmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("Nenhum codigo pendente para esse email"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Codigo expirado. Solicite um novo");
        }

        if (token.getAttempts() >= 5) {
            throw new RuntimeException("Muitas tentativas. Solicite um novo codigo");
        }

        token.setAttempts(token.getAttempts() + 1);
        tokenRepository.save(token);

        if (!codeService.verify(inputCode, token.getCodeHash())) {
            throw new RuntimeException("Codigo incorreto. Tentativas restantes: " + (5 - token.getAttempts()));
        }

        token.setUsed(true);
        tokenRepository.save(token);

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}