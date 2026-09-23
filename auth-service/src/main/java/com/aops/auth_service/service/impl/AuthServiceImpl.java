package com.aops.auth_service.service.impl;

import com.aops.auth_service.dto.request.LoginRequest;
import com.aops.auth_service.dto.request.RefreshTokenRequest;
import com.aops.auth_service.dto.request.RegisterRequest;
import com.aops.auth_service.dto.response.AuthResponse;
import com.aops.auth_service.model.Role;
import com.aops.auth_service.model.User;
import com.aops.auth_service.repository.UserRepository;
import com.aops.auth_service.security.JwtService;
import com.aops.auth_service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public void register(RegisterRequest request) {

        log.info("Registration attempt. username={}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {

            log.warn("Registration failed. Username already exists. username={}",
                    request.getUsername());

            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully. userId={}, username={}",
                savedUser.getId(),
                savedUser.getUsername());
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt. username={}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User authenticated successfully. userId={}, username={}",
                user.getId(),
                user.getUsername());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        log.info("Refresh token request received.");

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {

            log.warn("Refresh token validation failed.");

            throw new RuntimeException("Invalid refresh token");
        }

        Long userId = Long.valueOf(jwtService.extractUserId(refreshToken));

        User user = userRepository.findById(userId)
                .orElseThrow();

        String newAccessToken = jwtService.generateAccessToken(user);

        log.info("Access token refreshed successfully. userId={}", user.getId());

        return new AuthResponse(newAccessToken, refreshToken);
    }
}