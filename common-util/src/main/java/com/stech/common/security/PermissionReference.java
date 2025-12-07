package com.stech.common.security;

/**
 * REFERENCE ONLY - Permission slug naming conventions
 * 
 * This class provides documentation for permission slugs used in the database.
 * Actual permissions are managed in the database (permissions table) in authentication-service.
 * 
 * Permission Naming Convention: {RESOURCE}_{ACTION}
 * 
 * Examples:
 * - USER_READ, USER_WRITE, USER_UPDATE, USER_DELETE
 * - POST_READ, POST_WRITE, POST_UPDATE, POST_DELETE, POST_PUBLISH
 * - COMMENT_READ, COMMENT_WRITE, COMMENT_DELETE, COMMENT_MODERATE
 * - PAYMENT_READ, PAYMENT_WRITE, PAYMENT_PROCESS, PAYMENT_REFUND
 * 
 * Usage in code:
 * @PreAuthorize("hasAuthority('USER_READ')")  // Use slug directly
 * SecurityUtils.hasPermission("USER_READ")     // Use slug directly
 */
public final class PermissionReference {
    
    // User Management Permissions
    public static final String USER_READ = "USER_READ";
    public static final String USER_WRITE = "USER_WRITE";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    
    // Post Management Permissions
    public static final String POST_READ = "POST_READ";
    public static final String POST_WRITE = "POST_WRITE";
    public static final String POST_UPDATE = "POST_UPDATE";
    public static final String POST_DELETE = "POST_DELETE";
    public static final String POST_PUBLISH = "POST_PUBLISH";
    
    // Comment Management Permissions
    public static final String COMMENT_READ = "COMMENT_READ";
    public static final String COMMENT_WRITE = "COMMENT_WRITE";
    public static final String COMMENT_DELETE = "COMMENT_DELETE";
    public static final String COMMENT_MODERATE = "COMMENT_MODERATE";
    
    // Payment Permissions
    public static final String PAYMENT_READ = "PAYMENT_READ";
    public static final String PAYMENT_WRITE = "PAYMENT_WRITE";
    public static final String PAYMENT_PROCESS = "PAYMENT_PROCESS";
    public static final String PAYMENT_REFUND = "PAYMENT_REFUND";
    
    // Admin Permissions
    public static final String ADMIN_READ = "ADMIN_READ";
    public static final String ADMIN_WRITE = "ADMIN_WRITE";
    public static final String ADMIN_DELETE = "ADMIN_DELETE";
    public static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";
    
    // Analytics Permissions
    public static final String ANALYTICS_READ = "ANALYTICS_READ";
    public static final String ANALYTICS_WRITE = "ANALYTICS_WRITE";
    
    private PermissionReference() {
        // Private constructor to prevent instantiation
    }
}
