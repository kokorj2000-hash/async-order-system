package com.aops.auth_service.service;

import com.aops.auth_service.dto.request.LoginRequest;
import com.aops.auth_service.dto.request.RefreshTokenRequest;
import com.aops.auth_service.dto.request.RegisterRequest;
import com.aops.auth_service.dto.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}