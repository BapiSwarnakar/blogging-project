package com.stech.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stech.authentication.dto.request.LoginRequest;
import com.stech.authentication.dto.request.PermissionValidationRequest;
import com.stech.authentication.dto.request.RefreshTokenRequest;
import com.stech.authentication.dto.request.SignupRequest;
import com.stech.authentication.dto.response.JwtResponse;
import com.stech.authentication.dto.response.PermissionValidationResponse;
import com.stech.authentication.exception.CustomAuthException;
import com.stech.authentication.exception.CustomBadRequestException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.service.AuthService;
import com.stech.common.library.GlobalApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    private static final String AUTHENTICATION_FAILED_MESSAGE = "Authentication failed";
    private static final String BAD_REQUEST_MESSAGE = "Bad request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    private static final String TOKEN_EXPIRED_MESSAGE = "Token expired";
    private static final String TOKEN_NOT_FOUND_MESSAGE = "Token not found";

    private final AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user", 
        description = "Authenticate user with username and password"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Successfully authenticated",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalApiResponse.ApiResult.class)
        )
    )
    @ApiResponse(
        responseCode = "401", 
        description = "Unauthorized - Invalid credentials",
        content = @Content
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Bad Request - Invalid input",
        content = @Content
    )
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login credentials",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequest.class)
                )
            )
            @Valid @RequestBody LoginRequest loginRequest,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            log.info("Authenticating user: {}", loginRequest.getEmail());
            
            // Extract IP address and user agent
            String ipAddress = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest, ipAddress, userAgent);
            return ResponseEntity.ok(GlobalApiResponse.success(jwtResponse, "User authenticated successfully"));
            
        } catch (CustomAuthException e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error( e.getMessage(), AUTHENTICATION_FAILED_MESSAGE));
                
        } catch (CustomBadRequestException e) {
            log.error("Bad request during authentication: {}", e.getMessage());
            return ResponseEntity.status(400)
                .body(GlobalApiResponse.error(e.getMessage(), BAD_REQUEST_MESSAGE));
                
        } catch (CustomResourceNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error(e.getMessage(), AUTHENTICATION_FAILED_MESSAGE));
                
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, "An unexpected error occurred. Please try again later."));
        }
    }
    
    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register user", 
        description = "Register a new user"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "User registered successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalApiResponse.ApiResult.class)
        )
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Bad Request - Invalid input",
        content = @Content
    )
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Signup request",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SignupRequest.class)
                )
            )
            @Valid @RequestBody SignupRequest signUpRequest,
            HttpServletRequest request) {
        try {
            log.info("Registering new user: {}", signUpRequest.getFirstName());
            
            // Extract IP address and user agent
            String ipAddress = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            
            JwtResponse jwtResponse = authService.registerUser(signUpRequest, ipAddress, userAgent);
            return ResponseEntity.ok(GlobalApiResponse.success(jwtResponse, "User registered successfully"));
            
        } catch (com.stech.authentication.exception.CustomResourceAlreadyExistsException e) {
            log.error("User already exists: {}", e.getMessage());
            return ResponseEntity.status(409)
                .body(GlobalApiResponse.error("Registration failed", e.getMessage()));
                
        } catch (com.stech.authentication.exception.CustomBadRequestException e) {
            log.error("Bad request during registration: {}", e.getMessage());
            return ResponseEntity.status(400)
                .body(GlobalApiResponse.error(BAD_REQUEST_MESSAGE, e.getMessage()));
                
        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, "An unexpected error occurred during registration. Please try again later."));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> validateTokenAndPermissions(
            @RequestBody PermissionValidationRequest request) {
        try {
            log.info("Validating token and permissions");
            PermissionValidationResponse response = authService.validateTokenAndPermissions(request);
            
            if (!response.isValid()) {
                log.warn("Permission validation failed for token");
                return ResponseEntity.status(403).body(GlobalApiResponse.error("Permission validation failed", "Access forbidden"));
            }
            
            return ResponseEntity.ok(GlobalApiResponse.success(response, "Token and permissions validated successfully"));
            
        } catch (CustomAuthException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(GlobalApiResponse.error(INVALID_TOKEN_MESSAGE, e.getMessage()));

        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(GlobalApiResponse.error(INVALID_TOKEN_MESSAGE, "Token signature is invalid"));
            
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Token has expired: {}", e.getMessage());
            return ResponseEntity.status(401).body(GlobalApiResponse.error(TOKEN_EXPIRED_MESSAGE, "Token has expired"));
            
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Malformed token: {}", e.getMessage());
            return ResponseEntity.status(401).body(GlobalApiResponse.error(INVALID_TOKEN_MESSAGE, "Token format is invalid"));
            
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, "An unexpected error occurred during validation"));
        }
    }

    @PostMapping("/refresh-token")
    @Operation(
        summary = "Refresh access token",
        description = "Get a new access token using a valid refresh token"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully refreshed access token",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalApiResponse.ApiResult.class)
        )
    )
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or expired refresh token",
        content = @Content
    )
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Refresh token request",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RefreshTokenRequest.class)
                )
            )
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            log.info("Refreshing access token");
            // Extract IP address and user agent
            String ipAddress = getClientIp(httpServletRequest);
            String userAgent = httpServletRequest.getHeader("User-Agent");

            JwtResponse jwtResponse = authService.refreshAccessToken(request, ipAddress, userAgent);
            return ResponseEntity.ok(GlobalApiResponse.success(jwtResponse, "Access token refreshed successfully"));
            
        } catch (SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error("Invalid refresh token", "Invalid refresh token signature. The token may have been tampered with. Please login again."));
                
        } catch (ExpiredJwtException e) {
            log.error("Refresh token has expired: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error(TOKEN_EXPIRED_MESSAGE, "Refresh token has expired. Please login again."));
                
        } catch (MalformedJwtException e) {
            log.error("Malformed refresh token: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error(INVALID_TOKEN_MESSAGE, "Malformed refresh token. Please login again."));
                
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error("Unsupported token", "Unsupported token format. Please login again."));
                
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty or null: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error(INVALID_TOKEN_MESSAGE, "Invalid token. Please login again."));
                
        } catch (CustomAuthException e) {
            log.error("Authentication error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error(AUTHENTICATION_FAILED_MESSAGE, e.getMessage()));
                
        } catch (CustomResourceNotFoundException e) {
            log.error("Refresh token not found in database: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(GlobalApiResponse.error(TOKEN_NOT_FOUND_MESSAGE, "Refresh token not found or has been revoked. Please login again."));
                
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, "Failed to refresh access token. Please try again later."));
        }
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Logout user by invalidating refresh token"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully logged out",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalApiResponse.ApiResult.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request - Invalid input"
    )
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Refresh token to invalidate",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RefreshTokenRequest.class)
                )
            )
            @Valid @RequestBody RefreshTokenRequest request) {
        try {
            log.info("User logging out");
            authService.logout(request.getRefreshToken());
            return ResponseEntity.ok(GlobalApiResponse.success("Logged out successfully", "User logged out successfully"));
            
        } catch (CustomResourceNotFoundException e) {
            log.error("Refresh token not found during logout: {}", e.getMessage());
            return ResponseEntity.status(404)
                .body(GlobalApiResponse.error(TOKEN_NOT_FOUND_MESSAGE, "Refresh token not found or already invalidated"));
                
        } catch (CustomBadRequestException e) {
            log.error("Bad request during logout: {}", e.getMessage());
            return ResponseEntity.status(400)
                .body(GlobalApiResponse.error(BAD_REQUEST_MESSAGE, e.getMessage()));
                
        } catch (Exception e) {
            log.error("Unexpected error during logout: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(GlobalApiResponse.error(INTERNAL_SERVER_ERROR_MESSAGE, "An unexpected error occurred during logout. Please try again later."));
        }
    }
}
