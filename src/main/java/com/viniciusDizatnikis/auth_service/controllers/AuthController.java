package com.viniciusDizatnikis.auth_service.controllers;

import com.viniciusDizatnikis.auth_service.domain.user.User;
import com.viniciusDizatnikis.auth_service.dto.LoginRequestDTO;
import com.viniciusDizatnikis.auth_service.dto.RegisterRequestDTO;
import com.viniciusDizatnikis.auth_service.dto.ResponseDTO;
import com.viniciusDizatnikis.auth_service.infra.security.TokenService;
import com.viniciusDizatnikis.auth_service.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {

        User user = repository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean passwordMatches = passwordEncoder.matches(
                body.password(),
                user.getPassword()
        );

        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(
                new ResponseDTO(user.getName(), token)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {

        Optional<User> user = repository.findByEmail(body.email());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User newUser = new User();

        newUser.setName(body.name());
        newUser.setEmail(body.email());
        newUser.setPassword(
                passwordEncoder.encode(body.password())
        );

        repository.save(newUser);

        String token = tokenService.generateToken(newUser);

        return ResponseEntity.ok(
                new ResponseDTO(newUser.getName(), token)
        );
    }
}