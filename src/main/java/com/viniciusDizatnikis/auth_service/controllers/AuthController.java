package com.viniciusDizatnikis.auth_service.controllers;

import com.viniciusDizatnikis.auth_service.dto.*;
import com.viniciusDizatnikis.auth_service.service.AuthService;
import com.viniciusDizatnikis.auth_service.service.EmailVerificationService;
import com.viniciusDizatnikis.auth_service.service.PasswordResetService;
import com.viniciusDizatnikis.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService; // novo
    private final PasswordResetService passwordResetService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO dto) {
        String result = authService.register(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailDTO dto) {
        emailVerificationService.verifyCode(dto.email(), dto.code());
        return ResponseEntity.ok("Email verificado com sucesso");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody RequestPasswordResetDTO dto) {
        String code = passwordResetService.requestCode(dto.email());
        return ResponseEntity.ok("Codigo gerado: " + code); // temporario
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto.email(), dto.code(), dto.newPassword());
        return ResponseEntity.ok("Senha alterada com sucesso");
    }


    @PostMapping("/pre-register")
    public ResponseEntity<String> preRegister(@RequestBody RequestPasswordResetDTO dto) {
        String result = authService.preRegister(dto.email());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify-pre-register")
    public ResponseEntity<String> verifyPreRegister(@RequestBody VerifyEmailDTO dto) {
        authService.verifyPreRegisterCode(dto.email(), dto.code());
        return ResponseEntity.ok("Email verificado. Prossiga com o cadastro");
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDTO> refresh(@RequestBody RefreshRequestDTO dto) {
        String newAccessToken = refreshTokenService.refresh(dto.refreshToken());
        return ResponseEntity.ok(new RefreshResponseDTO(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshRequestDTO dto) {
        refreshTokenService.revoke(dto.refreshToken());
        return ResponseEntity.ok("Logout realizado com sucesso");
    }
}