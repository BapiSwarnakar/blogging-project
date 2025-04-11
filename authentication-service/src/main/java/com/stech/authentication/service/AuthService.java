package com.stech.authentication.service;

import com.stech.authentication.request.LoginRequest;
import com.stech.authentication.request.PermissionValidationRequest;
import com.stech.authentication.request.SignupRequest;
import com.stech.authentication.response.JwtResponse;
import com.stech.authentication.response.PermissionValidationResponse;
import com.stech.authentication.response.UserResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    UserResponse registerUser(SignupRequest signUpRequest);
    PermissionValidationResponse validateTokenAndPermissions(PermissionValidationRequest request);
}
