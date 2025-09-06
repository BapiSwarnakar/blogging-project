package com.stech.authentication.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

import com.stech.authentication.entity.PermissionEntity;
import com.stech.authentication.entity.RoleEntity;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.exception.CustomAuthException;
import com.stech.authentication.exception.CustomBadRequestException;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.helper.JwtTokenProvider;
import com.stech.authentication.repository.PermissionRepository;
import com.stech.authentication.repository.RoleRepository;
import com.stech.authentication.repository.UserRepository;
import com.stech.authentication.request.LoginRequest;
import com.stech.authentication.request.PermissionValidationRequest;
import com.stech.authentication.request.SignupRequest;
import com.stech.authentication.response.JwtResponse;
import com.stech.authentication.response.PermissionValidationResponse;
import com.stech.authentication.response.UserResponse;
import com.stech.authentication.service.AuthService;

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

    AuthServiceImpl(AuthenticationManager authenticationManager,
        UserRepository userRepository,
        RoleRepository roleRepository,
        PermissionRepository permissionRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider tokenProvider){
            this.authenticationManager = authenticationManager;
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.permissionRepository = permissionRepository;
            this.passwordEncoder = passwordEncoder;
            this.tokenProvider = tokenProvider;
    }

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        log.debug("Attempting to authenticate user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            String jwt = tokenProvider.generateToken(authentication);
            log.debug("Generated JWT for user: {}", userDetails.getUsername());
            
            return JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .username(userDetails.getUsername())
                .permissions(
                    userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(auth -> !auth.startsWith("ROLE_")) // Filter out role prefixes
                        .toList()
                )
                .build();
                
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getUsername());
            throw new CustomAuthException("Invalid username or password", e);
        } catch (DisabledException e) {
            log.error("Disabled account attempt: {}", loginRequest.getUsername());
            throw new CustomAuthException("User account is disabled", e);
        } catch (LockedException e) {
            log.error("Locked account attempt: {}", loginRequest.getUsername());
            throw new CustomAuthException("User account is locked", e);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw new CustomAuthException("Authentication failed", e);
        }
    }

    @Override
    public UserResponse registerUser(SignupRequest signUpRequest) {
        // Validate username/email
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new CustomBadRequestException("Username is already taken");
        }
        
        // Create user
        UserEntity userEntity = UserEntity.builder()
                    .username(signUpRequest.getUsername())
                    .email(signUpRequest.getEmail())
                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                    .build();


        // Set roles and permissions
        Set<RoleEntity> roles = resolveRoles(signUpRequest.getRoles());
        Set<PermissionEntity> directPermissions = resolvePermissions(signUpRequest.getDirectPermissions());

        userEntity.setRoles(roles);
        userEntity.setDirectPermissions(directPermissions);
        userRepository.save(userEntity);

        return UserResponse.builder()
            .id(userEntity.getId())
            .email(userEntity.getEmail())
            .username(userEntity.getUsername())
            .permissions(getCombinedPermissions(userEntity))
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

        UserEntity userEntity = userRepository.findByUsername(username)
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
            return Set.of(roleRepository.findByName("ROLE_USER")
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
            role.getPermissions().forEach(perm -> permissions.add(perm.getName()));
        });
        
        // Add direct permissions
        user.getDirectPermissions().forEach(perm -> permissions.add(perm.getName()));
        
        return permissions;
    }

    private boolean getCombinedValidationPermissions(UserEntity user, String apiUrl, String method) {
        AtomicBoolean hasAccess = new AtomicBoolean(false);
        // Add role-based permissions
        user.getRoles().forEach(role -> {
            if (role.isFullAccess()) {
                hasAccess.set(true);
                return; // Full access overrides all checks
            }
            // Check if role has permission for the given API URL and method
            role.getPermissions().forEach(perm -> {
                if (perm.getApiUrl().equals(apiUrl) && perm.getApiMethod().equalsIgnoreCase(method)) {
                    hasAccess.set(true);
                }
            });
        });

        // Add direct permissions
        user.getDirectPermissions().forEach(perm -> {
            if (perm.getApiUrl().equals(apiUrl) && perm.getApiMethod().equalsIgnoreCase(method)) {
                hasAccess.set(true);
            }
        });
        return hasAccess.get();
    }
}
