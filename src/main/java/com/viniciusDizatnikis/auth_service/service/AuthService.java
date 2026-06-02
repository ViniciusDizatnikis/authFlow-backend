package com.viniciusDizatnikis.auth_service.service;

import com.viniciusDizatnikis.auth_service.domain.token.PendingEmailToken;
import com.viniciusDizatnikis.auth_service.domain.token.RefreshToken;
import com.viniciusDizatnikis.auth_service.domain.user.User;
import com.viniciusDizatnikis.auth_service.dto.LoginRequestDTO;
import com.viniciusDizatnikis.auth_service.dto.LoginResponseDTO;
import com.viniciusDizatnikis.auth_service.dto.register.RegisterDTO;
import com.viniciusDizatnikis.auth_service.dto.register.PreRegisterResponseDTO;
import com.viniciusDizatnikis.auth_service.dto.register.PreVerificationResponseDTO;
import com.viniciusDizatnikis.auth_service.dto.register.RegisterResponseDTO;
import com.viniciusDizatnikis.auth_service.exception.*;
import com.viniciusDizatnikis.auth_service.infra.security.CodeService;
import com.viniciusDizatnikis.auth_service.infra.security.TokenService;
import com.viniciusDizatnikis.auth_service.repositories.EmailVerificationTokenRepository;
import com.viniciusDizatnikis.auth_service.repositories.PendingEmailTokenRepository;
import com.viniciusDizatnikis.auth_service.repositories.RefreshTokenRepository;
import com.viniciusDizatnikis.auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final TokenService tokenService;
    private final CodeService codeService;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final PendingEmailTokenRepository pendingEmailTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public PreRegisterResponseDTO preRegister(String email) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email já cadastrado");
        }

        String code = codeService.generateCode();

        PendingEmailToken token = new PendingEmailToken();
        token.setEmail(email);
        token.setCodeHash(codeService.hashCode(code));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        pendingEmailTokenRepository.save(token);

        emailService.sendVerificationCode(email, code);

        return new PreRegisterResponseDTO(
                200,
                "EMAIL_VERIFICATION_SENT",
                "Código de verificação enviado com sucesso.",
                email,
                token.getExpiresAt()
        );
    }

    public PreVerificationResponseDTO verifyPreRegisterCode(String email, String inputCode) {
        PendingEmailToken token = pendingEmailTokenRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new EmailVerificationTokenNotFound(
                        "Nenhum token ativo encontrado para o e-mail: " + email
                ));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Codigo expirado");
        }

        if (token.getAttempts() >= 5) {
            throw new TooManyAttemptsException("Muitas tentativas. Solicite um novo codigo");
        }

        token.setAttempts(token.getAttempts() + 1);
        pendingEmailTokenRepository.save(token);

        if (!codeService.verify(inputCode, token.getCodeHash())) {
            throw new InvalidVerificationCodeException(
                    "Código incorreto. Tentativas restantes: " + (5 - token.getAttempts())
            );
        }

        // marca como verificado
        token.setVerified(true);
        pendingEmailTokenRepository.save(token);

        return new PreVerificationResponseDTO(
                200,
                "EMAIL_VERIFIED",
                "E-mail verificado com sucesso.",
                email,
                true
        );
    }

    public RegisterResponseDTO register(RegisterDTO dto) {

        PendingEmailToken pending = pendingEmailTokenRepository
                .findTopByEmailAndVerifiedTrueAndUsedFalseOrderByCreatedAtDesc(dto.email())
                .orElseThrow(() ->
                        new EmailNotVerified(
                                "Email nao verificado. Complete a verificacao primeiro"
                        )
                );

        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new EmailAlreadyRegistered("Email já cadastrado");
        }

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setEmailVerified(true);

        userRepository.save(user);

        pending.setUsed(true);
        pendingEmailTokenRepository.save(pending);

        return new RegisterResponseDTO(
                201,
                "USER_CREATED",
                "Conta criada com sucesso.",
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Email ou senha incorretos"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new RuntimeException("Email ou senha incorretos");
        }

        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken.getToken(), user.getName());
    }


}