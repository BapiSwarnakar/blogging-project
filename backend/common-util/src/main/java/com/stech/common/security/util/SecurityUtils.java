package com.stech.common.security.util;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security Utility class for common security operations
 * Works with database-driven roles and permissions
 * Authorities are loaded from JWT tokens which contain roles and permission slugs
 */
public final class SecurityUtils {

    public static final String ROLE_PREFIX = "ASSIGN_ROLE_";

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the current authenticated username
     * @return username or null if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Get the current user's authorities (roles and permissions)
     * @return list of authority names
     */
    public static List<String> getCurrentAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }
        return List.of();
    }

    /**
     * Check if current user has a specific role
     * @param roleName the role name to check (without ROLE_ prefix)
     * @return true if user has the role
     */
    public static boolean hasRole(String roleName) {
        String roleWithPrefix = roleName.startsWith(ROLE_PREFIX) ? roleName : ROLE_PREFIX + roleName;
        return getCurrentAuthorities().contains(roleWithPrefix);
    }

    /**
     * Check if current user has any of the specified roles
     * @param roleNames role names to check (without ROLE_ prefix)
     * @return true if user has any of the roles
     */
    public static boolean hasAnyRole(String... roleNames) {
        List<String> currentAuthorities = getCurrentAuthorities();
        for (String roleName : roleNames) {
            String roleWithPrefix = roleName.startsWith(ROLE_PREFIX) ? roleName : ROLE_PREFIX + roleName;
            if (currentAuthorities.contains(roleWithPrefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has all of the specified roles
     * @param roleNames role names to check (without ROLE_ prefix)
     * @return true if user has all of the roles
     */
    public static boolean hasAllRoles(String... roleNames) {
        List<String> currentAuthorities = getCurrentAuthorities();
        for (String roleName : roleNames) {
            String roleWithPrefix = roleName.startsWith(ROLE_PREFIX) ? roleName : ROLE_PREFIX + roleName;
            if (!currentAuthorities.contains(roleWithPrefix)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if current user has a specific permission (by slug)
     * @param permissionSlug the permission slug to check (e.g., "USER_READ")
     * @return true if user has the permission
     */
    public static boolean hasPermission(String permissionSlug) {
        return getCurrentAuthorities().contains(permissionSlug);
    }

    /**
     * Check if current user has any of the specified permissions
     * @param permissionSlugs permission slugs to check
     * @return true if user has any of the permissions
     */
    public static boolean hasAnyPermission(String... permissionSlugs) {
        List<String> currentAuthorities = getCurrentAuthorities();
        for (String permission : permissionSlugs) {
            if (currentAuthorities.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has all of the specified permissions
     * @param permissionSlugs permission slugs to check
     * @return true if user has all of the permissions
     */
    public static boolean hasAllPermissions(String... permissionSlugs) {
        List<String> currentAuthorities = getCurrentAuthorities();
        for (String permission : permissionSlugs) {
            if (!currentAuthorities.contains(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if user is authenticated
     * @return true if authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Check if current user is an admin
     * @return true if user has admin role
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user has a specific authority (role or permission)
     * @param authority the authority to check
     * @return true if user has the authority
     */
    public static boolean hasAuthority(String authority) {
        return getCurrentAuthorities().contains(authority);
    }

    /**
     * Check if current user has any of the specified authorities
     * @param authorities authorities to check
     * @return true if user has any of the authorities
     */
    public static boolean hasAnyAuthority(String... authorities) {
        List<String> currentAuthorities = getCurrentAuthorities();
        for (String authority : authorities) {
            if (currentAuthorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the Authentication object
     * @return current authentication or null
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get all roles for current user (with ROLE_ prefix)
     * @return list of role names
     */
    public static List<String> getCurrentRoles() {
        return getCurrentAuthorities().stream()
                .filter(auth -> auth.startsWith(ROLE_PREFIX))
                .toList();
    }

    /**
     * Get all permissions for current user (without ROLE_ prefix)
     * @return list of permission slugs
     */
    public static List<String> getCurrentPermissions() {
        return getCurrentAuthorities().stream()
                .filter(auth -> !auth.startsWith(ROLE_PREFIX))
                .toList();
    }
}
