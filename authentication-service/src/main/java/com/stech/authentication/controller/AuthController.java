package com.stech.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.stech.authentication.dto.request.LoginRequest;
import com.stech.authentication.dto.request.PermissionValidationRequest;
import com.stech.authentication.dto.request.SignupRequest;
import com.stech.authentication.dto.response.JwtResponse;
import com.stech.authentication.dto.response.PermissionValidationResponse;
import com.stech.authentication.dto.response.UserResponse;
import com.stech.authentication.service.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(
    name = "Authentication", 
    description = "APIs for user authentication and authorization"
)
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user", 
        description = "Authenticate user with username and password"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully authenticated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - Invalid credentials",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Bad Request - Invalid input",
            content = @Content
        )
    })
    public ResponseEntity<JwtResponse> authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login credentials",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequest.class)
                )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register user", 
        description = "Register a new user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Bad Request - Invalid input",
            content = @Content
        )
    })
    public ResponseEntity<UserResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Signup request",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SignupRequest.class)
                )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody SignupRequest signUpRequest) {
        log.info(signUpRequest.toString());
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
