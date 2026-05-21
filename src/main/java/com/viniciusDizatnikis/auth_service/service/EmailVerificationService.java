package com.viniciusDizatnikis.auth_service.service;

import com.viniciusDizatnikis.auth_service.domain.token.EmailVerificationToken;
import com.viniciusDizatnikis.auth_service.domain.user.User;
import com.viniciusDizatnikis.auth_service.infra.security.CodeService;
import com.viniciusDizatnikis.auth_service.repositories.EmailVerificationTokenRepository;
import com.viniciusDizatnikis.auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final CodeService codeService;

    private final EmailService emailService;

    // chamado automaticamente no register — ja existe no AuthService
    // esse metodo e util se o usuario pedir reenvio do codigo
    public String generateCode(User user) {
        String code = codeService.generateCode();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setCodeHash(codeService.hashCode(code));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(token);

        emailService.sendVerificationCode(user.getEmail(), code); // envia de verdade

        return "Codigo enviado para o email";
    }

    public void verifyCode(String email, String inputCode) {
        EmailVerificationToken token = tokenRepository
                .findTopByUser_EmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("Nenhum codigo pendente para esse email"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Codigo expirado");
        }

        if (token.getAttempts() >= 5) {
            throw new RuntimeException("Muitas tentativas. Solicite um novo codigo");
        }

        // incrementa tentativa ANTES de validar — evita timing attack
        token.setAttempts(token.getAttempts() + 1);
        tokenRepository.save(token);

        if (!codeService.verify(inputCode, token.getCodeHash())) {
            throw new RuntimeException("Codigo incorreto. Tentativas restantes: " + (5 - token.getAttempts()));
        }

        // codigo correto — marca como usado e verifica o email do usuario
        token.setUsed(true);
        tokenRepository.save(token);

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
    }
}