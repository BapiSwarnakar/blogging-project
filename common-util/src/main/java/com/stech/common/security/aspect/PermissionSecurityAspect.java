package com.stech.common.security.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.stech.common.security.annotation.RequirePermission;

@Aspect
@Component
@Slf4j
public class PermissionSecurityAspect {

    public PermissionSecurityAspect() {
        log.info("Constant checking: PermissionSecurityAspect initialized");
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
             throw new AccessDeniedException("User is not authenticated");
        }

        // 1. Check for basic authentication
        if ("anonymousUser".equals(authentication.getPrincipal())) {
             throw new AccessDeniedException("User is not authenticated");
        }

        // 2. Check for FULL_ACCESS
        boolean isFullAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> "FULL_ACCESS".equals(a.getAuthority()));

        if (isFullAccess) {
            log.info("Access granted via FULL_ACCESS for user: {}", authentication.getName());
            return;
        }

        // 3. Check for specific permission
        String rawPermission = requirePermission.authority();
        if (rawPermission == null || rawPermission.isEmpty()) {
            // Fallback to 'value' alias if authority is empty
            rawPermission = requirePermission.value();
        }
        final String requiredPermission = rawPermission;

        log.info("Checking permission '{}' for user '{}'", requiredPermission, authentication.getName());
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(requiredPermission));

        if (!hasPermission) {
            log.warn("Access Denied. User '{}' does not have required permission '{}'", authentication.getName(), requiredPermission);
            throw new AccessDeniedException("Access Denied");
        }
        
        log.info("Access granted for permission '{}'", requiredPermission);
    }
}
