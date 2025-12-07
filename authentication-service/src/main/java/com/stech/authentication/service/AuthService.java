package com.stech.authentication.service;

import com.stech.authentication.dto.request.LoginRequest;
import com.stech.authentication.dto.request.PermissionValidationRequest;
import com.stech.authentication.dto.request.RefreshTokenRequest;
import com.stech.authentication.dto.request.SignupRequest;
import com.stech.authentication.dto.response.JwtResponse;
import com.stech.authentication.dto.response.PermissionValidationResponse;
import com.stech.authentication.dto.response.UserResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest, String ipAddress, String userAgent);
    UserResponse registerUser(SignupRequest signUpRequest);
    PermissionValidationResponse validateTokenAndPermissions(PermissionValidationRequest request);
    JwtResponse refreshAccessToken(RefreshTokenRequest request);
    void logout(String refreshToken);
}
