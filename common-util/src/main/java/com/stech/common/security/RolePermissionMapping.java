package com.stech.common.security;

/**
 * DEPRECATED - This class is not used in database-driven approach
 * 
 * Role-permission mappings are now managed in the database through the
 * role_permissions table in the authentication-service.
 * 
 * To manage role-permission mappings:
 * 1. Use the authentication-service REST APIs
 * 2. Directly update the role_permissions table
 * 3. Build an admin panel for permission management
 * 
 * This file is kept for reference only and may be removed in future versions.
 */
@Deprecated
public final class RolePermissionMapping {
    
    private RolePermissionMapping() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * @deprecated Use database queries instead
     */
    @Deprecated
    public static java.util.List<String> getPermissionsForRole(String roleName) {
        throw new UnsupportedOperationException(
            "Role-permission mappings are managed in the database. " +
            "Query the role_permissions table in authentication-service."
        );
    }
    
    /**
     * @deprecated Use database queries instead
     */
    @Deprecated
    public static boolean hasPermission(String roleName, String permission) {
        throw new UnsupportedOperationException(
            "Role-permission mappings are managed in the database. " +
            "Query the role_permissions table in authentication-service."
        );
    }
}
