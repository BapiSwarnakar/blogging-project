package com.stech.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.authentication.request.LoginRequest;
import com.stech.authentication.request.PermissionValidationRequest;
import com.stech.authentication.request.SignupRequest;
import com.stech.authentication.response.JwtResponse;
import com.stech.authentication.response.PermissionValidationResponse;
import com.stech.authentication.response.UserResponse;
import com.stech.authentication.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<PermissionValidationResponse> validateTokenAndPermissions(
            @RequestBody PermissionValidationRequest request) {
        PermissionValidationResponse response = authService.validateTokenAndPermissions(request);
        if (!response.isValid()) {
            return ResponseEntity.status(403).body(response); // Forbidden
        }
        return ResponseEntity.ok(response);
    }
}
