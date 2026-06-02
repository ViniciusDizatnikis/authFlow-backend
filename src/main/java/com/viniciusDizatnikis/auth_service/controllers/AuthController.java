package com.viniciusDizatnikis.auth_service.controllers;

import com.viniciusDizatnikis.auth_service.dto.*;
import com.viniciusDizatnikis.auth_service.dto.register.*;
import com.viniciusDizatnikis.auth_service.dto.VerifyEmailDTO;
import com.viniciusDizatnikis.auth_service.dto.VerifyEmailResponseDTO;
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
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final RefreshTokenService refreshTokenService;


    //Enviar token para o email para validar
    @PostMapping("/pre-register")
    public ResponseEntity<PreRegisterResponseDTO> preRegister(@RequestBody PreRegisterDTO dto) {
        return ResponseEntity.ok(
                authService.preRegister(dto.email())
        );
    }

    //Validar token do pre registro
    @PostMapping("/verify-pre-register")
    public ResponseEntity<PreVerificationResponseDTO> verifyPreRegister(@RequestBody VerifyEmailDTO dto) {
        return ResponseEntity.ok(
                authService.verifyPreRegisterCode(dto.email(), dto.code())
        );
    }

    //Finalizar Registro
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(
                authService.register(dto)
        );
    }







    //Validar o code do usuário
    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponseDTO> verifyEmail(@RequestBody VerifyEmailDTO dto) {
        VerifyEmailResponseDTO result =
                emailVerificationService.verifyCode(
                        dto.email(),
                        dto.code()
                );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
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