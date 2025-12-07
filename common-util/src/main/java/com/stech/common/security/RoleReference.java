package com.stech.common.security;

/**
 * REFERENCE ONLY - Role naming conventions
 * 
 * This class provides documentation for role names used in the database.
 * Actual roles are managed in the database (roles table) in authentication-service.
 * 
 * Role names in database should match these conventions:
 * - ADMIN
 * - USER
 * - AUTHOR
 * - EDITOR
 * - MODERATOR
 * - GUEST
 * - SYSTEM
 * 
 * In Spring Security, roles are automatically prefixed with "ROLE_"
 * Example: "ADMIN" becomes "ROLE_ADMIN" in authorities
 * 
 * Usage in code:
 * @PreAuthorize("hasRole('ADMIN')")  // Spring adds ROLE_ prefix
 * SecurityUtils.hasRole("ADMIN")     // Utility method adds ROLE_ prefix
 */
public final class RoleReference {
    
    // Role name constants for reference
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String AUTHOR = "AUTHOR";
    public static final String EDITOR = "EDITOR";
    public static final String MODERATOR = "MODERATOR";
    public static final String GUEST = "GUEST";
    public static final String SYSTEM = "SYSTEM";
    
    private RoleReference() {
        // Private constructor to prevent instantiation
    }
}
