package com.stech.authentication.service;

import com.stech.authentication.dto.request.LoginRequest;
import com.stech.authentication.dto.request.PermissionValidationRequest;
import com.stech.authentication.dto.request.RefreshTokenRequest;
import com.stech.authentication.dto.request.SignupRequest;
import com.stech.authentication.dto.response.JwtResponse;
import com.stech.authentication.dto.response.PermissionValidationResponse;


public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest, String ipAddress, String userAgent);
    JwtResponse registerUser(SignupRequest signUpRequest, String ipAddress, String userAgent);
    PermissionValidationResponse validateTokenAndPermissions(PermissionValidationRequest request);
    JwtResponse refreshAccessToken(RefreshTokenRequest request, String ipAddress, String userAgent);
    void logout(String refreshToken);
}
