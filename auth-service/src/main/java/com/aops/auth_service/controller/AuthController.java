package com.aops.auth_service.controller;

import com.aops.auth_service.dto.request.LoginRequest;
import com.aops.auth_service.dto.request.RefreshTokenRequest;
import com.aops.auth_service.dto.request.RegisterRequest;
import com.aops.auth_service.dto.response.AuthResponse;
import com.aops.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                authService.refreshToken(request)
        );
    }
}