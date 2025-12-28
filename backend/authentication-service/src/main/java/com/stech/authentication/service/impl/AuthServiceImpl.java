package com.stech.authentication.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stech.authentication.dto.request.LoginRequest;
import com.stech.authentication.dto.request.PermissionValidationRequest;
import com.stech.authentication.dto.request.RefreshTokenRequest;
import com.stech.authentication.dto.request.SignupRequest;
import com.stech.authentication.dto.response.JwtResponse;
import com.stech.authentication.dto.response.PermissionValidationResponse;
import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.RefreshTokenEntity;
import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.exception.CustomAuthException;
import com.stech.authentication.exception.CustomBadRequestException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.helper.JwtTokenProvider;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.repository.RoleRepository;
import com.stech.authentication.repository.UserRepository;
import com.stech.authentication.service.AuthService;
import com.stech.authentication.service.RefreshTokenService;
import com.stech.common.security.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private static final String ASSIGN_ROLE_PREFIX = SecurityUtils.ROLE_PREFIX;

    AuthServiceImpl(AuthenticationManager authenticationManager,
        UserRepository userRepository,
        RoleRepository roleRepository,
        PermissionRepository permissionRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider tokenProvider,
        RefreshTokenService refreshTokenService){
            this.authenticationManager = authenticationManager;
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.permissionRepository = permissionRepository;
            this.passwordEncoder = passwordEncoder;
            this.tokenProvider = tokenProvider;
            this.refreshTokenService = refreshTokenService;
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest, String ipAddress, String userAgent) {
        log.debug("Attempting to authenticate user: {}", loginRequest.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Generate access token
            String accessToken = tokenProvider.generateToken(authentication, ipAddress, userAgent);
            
            // Generate refresh token
            RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(
                userDetails.getEmail(), ipAddress, userAgent
            );
            
            // Extract roles and permissions
            var authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            
            var roles = authorities.stream()
                .filter(auth -> auth.startsWith(ASSIGN_ROLE_PREFIX))
                .map(auth -> auth.substring(12)) // Remove ROLE_ prefix
                .toList();
            
            var permissions = authorities.stream()
                .filter(auth -> !auth.startsWith(ASSIGN_ROLE_PREFIX))
                .toList();
            
            return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationInMilliseconds() / 1000) // Convert to seconds
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .name(userDetails.getUsername())
                .gender(userDetails.getGender())
                .permissions(permissions)
                .roles(roles)
                .build();
                
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getEmail());
            throw new CustomAuthException("Invalid username or password", e);
        } catch (DisabledException e) {
            log.error("Disabled account attempt: {}", loginRequest.getEmail());
            throw new CustomAuthException("User account is disabled", e);
        } catch (LockedException e) {
            log.error("Locked account attempt: {}", loginRequest.getEmail());
            throw new CustomAuthException("User account is locked", e);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getEmail(), e);
            throw new CustomAuthException("Authentication failed", e);
        }
    }

    @Override
    @Transactional
    public JwtResponse registerUser(SignupRequest signUpRequest, String ipAddress, String userAgent) {
        // Validate username/email
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new CustomBadRequestException("Email is already taken");
        }
        
        // Create user
        log.debug("Creating user: {}", signUpRequest);
        UserEntity userEntity = UserEntity.builder()
                    .firstName(signUpRequest.getFirstName())
                    .middleName(signUpRequest.getMiddleName())
                    .lastName(signUpRequest.getLastName())
                    .gender(signUpRequest.getGender())
                    .phone(signUpRequest.getPhone())
                    .dateOfBirth(signUpRequest.getDateOfBirth())
                    .email(signUpRequest.getEmail())
                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                    .build();

        
        log.debug("User created: {}", userEntity);
        // Set roles and permissions
        Set<RoleEntity> roles = resolveRoles(signUpRequest.getRoles());
        log.debug("Roles resolved: {}", roles);
        Set<PermissionEntity> directPermissions = resolvePermissions(signUpRequest.getDirectPermissions());
        log.debug("Permissions resolved: {}", directPermissions);

        userEntity.setRoles(roles);
        userEntity.setDirectPermissions(directPermissions);
        userEntity = userRepository.save(userEntity);

        // Authenticate the user (generate tokens)
        CustomUserDetails userDetails = new CustomUserDetails(userEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        
        // Generate access token
        String accessToken = tokenProvider.generateToken(authentication, ipAddress, userAgent);
        
        // Generate refresh token
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(
            userEntity.getEmail(), ipAddress, userAgent
        );
        
        // Extract roles and permissions
        var authorities = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        
        var roleNames = authorities.stream()
            .filter(auth -> auth.startsWith(ASSIGN_ROLE_PREFIX))
            .map(auth -> auth.substring(12)) // Remove ROLE_ prefix
            .toList();
        
        var permissions = authorities.stream()
            .filter(auth -> !auth.startsWith(ASSIGN_ROLE_PREFIX))
            .toList();

        return JwtResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .tokenType("Bearer")
            .expiresIn(tokenProvider.getExpirationInMilliseconds() / 1000)
            .id(userEntity.getId())
            .email(userEntity.getEmail())
            .name(userEntity.getName())
            .gender(userEntity.getGender())
            .permissions(permissions)
            .roles(roleNames)
            .build();
    }

    @Override
    public PermissionValidationResponse validateTokenAndPermissions(PermissionValidationRequest request) {
        String username = tokenProvider.getUsernameFromToken(request.getToken());
        
        if (!tokenProvider.validateToken(request.getToken())) {
            return PermissionValidationResponse.builder()
                    .isValid(false)
                    .message("Invalid token")
                    .userPermissions(null)
                    .ipAddress(request.getIpAddress())
                    .userId(null)
                    .build();
        }

        if (!tokenProvider.isAccessToken(request.getToken())) {
            throw new CustomAuthException("Invalid token type. Only access tokens are allowed.");
        }

        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(()-> new CustomResourceNotFoundException("User not found"));

        // boolean hasAccess = getCombinedValidationPermissions(userEntity, request.getRequiredPermissionsApi(), request.getRequiredPermissionsMethod());
        
        Set<String> userPermissions = getCombinedPermissions(userEntity);

        return PermissionValidationResponse.builder()
                .isValid(true)
                .message("Access granted")
                .userPermissions(userPermissions)
                .ipAddress(request.getIpAddress())
                .userId(userEntity.getId())
                .build();
    }

    private Set<RoleEntity> resolveRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of(roleRepository.findByName("USER")
                .orElseThrow(() -> new CustomResourceNotFoundException("Default role not found")));
        }

        return roleNames.stream()
            .map(roleName -> roleRepository.findByName(roleName)
                .orElseThrow(() -> new CustomResourceNotFoundException("Role not found: " + roleName)))
            .collect(Collectors.toSet());
    }

    private Set<PermissionEntity> resolvePermissions(Set<String> permissionNames) {
        if (permissionNames == null || permissionNames.isEmpty()) {
            return Set.of();
        }

        return permissionNames.stream()
            .map(permName -> permissionRepository.findByName(permName)
                .orElseThrow(() -> new CustomResourceNotFoundException("Permission not found: " + permName)))
            .collect(Collectors.toSet());
    }

    private Set<String> getCombinedPermissions(UserEntity user) {
        Set<String> permissions = new HashSet<>();
        
        // Add role-based permissions
        user.getRoles().forEach(role -> {
            role.getPermissions().forEach(perm -> permissions.add(perm.getSlug()));
        });
        
        // Add direct permissions
        user.getDirectPermissions().forEach(perm -> permissions.add(perm.getSlug()));
        
        return permissions;
    }



    @Override
    public JwtResponse refreshAccessToken(RefreshTokenRequest request, String ipAddress, String userAgent) {
        String refreshTokenString = request.getRefreshToken();
        
        try {
            // Validate refresh token format
            if (!tokenProvider.validateToken(refreshTokenString)) {
                log.error("Invalid refresh token provided");
                throw new CustomAuthException("Invalid or expired refresh token. Please login again.");
            }
            
            // Check if it's actually a refresh token
            if (!tokenProvider.isRefreshToken(refreshTokenString)) {
                log.error("Provided token is not a refresh token");
                throw new CustomAuthException("Invalid token type. Please provide a refresh token.");
            }
            
            // Find and verify refresh token in database
            RefreshTokenEntity refreshToken = refreshTokenService.findByToken(refreshTokenString);
            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            
            UserEntity user = refreshToken.getUser();
            
            // Load user details to generate new access token
            CustomUserDetails userDetails = new CustomUserDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
            
            // Generate new access token
            String newAccessToken = tokenProvider.generateToken(authentication, ipAddress, userAgent);
            log.info("Refreshed access token for user: {}", user.getName());
            
            // Extract roles and permissions
            var authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            
            var roles = authorities.stream()
                .filter(auth -> auth.startsWith(ASSIGN_ROLE_PREFIX))
                .map(auth -> auth.substring(12))
                .toList();
            
            var permissions = authorities.stream()
                .filter(auth -> !auth.startsWith(ASSIGN_ROLE_PREFIX))
                .toList();
            
            return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenString) // Return same refresh token
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationInMilliseconds() / 1000)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender())
                .permissions(permissions)
                .roles(roles)
                .build();
                
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            throw new CustomAuthException("Invalid refresh token signature. The token may have been tampered with. Please login again.");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Refresh token has expired: {}", e.getMessage());
            throw new CustomAuthException("Refresh token has expired. Please login again.");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Malformed refresh token: {}", e.getMessage());
            throw new CustomAuthException("Malformed refresh token. Please login again.");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new CustomAuthException("Unsupported token format. Please login again.");
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty or null: {}", e.getMessage());
            throw new CustomAuthException("Invalid token. Please login again.");
        } catch (CustomAuthException e) {
            // Re-throw custom auth exceptions
            throw e;
        } catch (CustomResourceNotFoundException e) {
            log.error("Refresh token not found in database: {}", e.getMessage());
            throw new CustomAuthException("Refresh token not found or has been revoked. Please login again.");
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            throw new CustomAuthException("Failed to refresh access token. Please login again.");
        }
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                refreshTokenService.deleteByToken(refreshToken);
                log.info("User logged out successfully");
            } catch (Exception e) {
                log.warn("Failed to delete refresh token during logout: {}", e.getMessage());
            }
        }
    }
}
