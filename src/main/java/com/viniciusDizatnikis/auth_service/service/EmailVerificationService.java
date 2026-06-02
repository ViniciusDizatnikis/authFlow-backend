package com.viniciusDizatnikis.auth_service.service;

import com.viniciusDizatnikis.auth_service.domain.token.EmailVerificationToken;
import com.viniciusDizatnikis.auth_service.domain.user.User;
import com.viniciusDizatnikis.auth_service.dto.VerifyEmailResponseDTO;
import com.viniciusDizatnikis.auth_service.exception.EmailVerificationTokenNotFound;
import com.viniciusDizatnikis.auth_service.exception.InvalidVerificationCodeException;
import com.viniciusDizatnikis.auth_service.exception.TokenExpiredException;
import com.viniciusDizatnikis.auth_service.exception.TooManyAttemptsException;
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

    public VerifyEmailResponseDTO verifyCode(String email, String inputCode) {

        EmailVerificationToken token = tokenRepository
                .findTopByUser_EmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() ->
                        new EmailVerificationTokenNotFound(
                                "Nenhum token ativo encontrado para o e-mail: " + email
                        )
                );


        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Codigo expirado");
        }

        if (token.getAttempts() >= 5) {
            throw new TooManyAttemptsException("Muitas tentativas. Solicite um novo codigo");
        }


        // incrementa tentativa ANTES de validar
        token.setAttempts(token.getAttempts() + 1);
        tokenRepository.save(token);


        if (!codeService.verify(inputCode, token.getCodeHash())) {
            throw new InvalidVerificationCodeException(
                    "Código incorreto. Tentativas restantes: " + (5 - token.getAttempts())
            );
        }

        // codigo correto — marca como usado e verifica o email do usuario
        token.setUsed(true);
        tokenRepository.save(token);

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        return new VerifyEmailResponseDTO(
                200,
                "EMAIL_VERIFIED",
                "E-mail verificado com sucesso."
        );
    }
}