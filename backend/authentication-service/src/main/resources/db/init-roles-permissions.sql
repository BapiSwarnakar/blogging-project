-- Initial Roles and Permissions Setup for Blogging Platform
-- Run this script in your authentication-service database

-- ============================================
-- 1. CREATE ROLES
-- ============================================

INSERT INTO roles (name, description, is_active, is_full_access) VALUES
('ADMIN', 'System Administrator with full access', true, true),
('USER', 'Regular user with basic permissions', true, false),
('AUTHOR', 'Blog post author', true, false),
('EDITOR', 'Blog editor with publishing rights', true, false),
('MODERATOR', 'Content moderator', true, false),
('GUEST', 'Guest user with read-only access', true, false);

-- ============================================
-- 2. CREATE PERMISSIONS
-- ============================================

-- User Management Permissions
INSERT INTO permissions (name, category, slug, api_url, api_method, description) VALUES
('Read User', 'USER_MANAGEMENT', 'USER_READ', '/api/v1/users/*', 'GET', 'View user information'),
('Create User', 'USER_MANAGEMENT', 'USER_WRITE', '/api/v1/users', 'POST', 'Create new user'),
('Update User', 'USER_MANAGEMENT', 'USER_UPDATE', '/api/v1/users/*', 'PUT', 'Update user information'),
('Delete User', 'USER_MANAGEMENT', 'USER_DELETE', '/api/v1/users/*', 'DELETE', 'Delete user account');

-- Post Management Permissions
INSERT INTO permissions (name, category, slug, api_url, api_method, description) VALUES
('Read Post', 'POST_MANAGEMENT', 'POST_READ', '/api/v1/posts/*', 'GET', 'View blog posts'),
('Create Post', 'POST_MANAGEMENT', 'POST_WRITE', '/api/v1/posts', 'POST', 'Create new blog post'),
('Update Post', 'POST_MANAGEMENT', 'POST_UPDATE', '/api/v1/posts/*', 'PUT', 'Update blog post'),
('Delete Post', 'POST_MANAGEMENT', 'POST_DELETE', '/api/v1/posts/*', 'DELETE', 'Delete blog post'),
('Publish Post', 'POST_MANAGEMENT', 'POST_PUBLISH', '/api/v1/posts/*/publish', 'POST', 'Publish blog post');

-- Comment Management Permissions
INSERT INTO permissions (name, category, slug, api_url, api_method, description) VALUES
('Read Comment', 'COMMENT_MANAGEMENT', 'COMMENT_READ', '/api/v1/comments/*', 'GET', 'View comments'),
('Create Comment', 'COMMENT_MANAGEMENT', 'COMMENT_WRITE', '/api/v1/comments', 'POST', 'Create new comment'),
('Delete Comment', 'COMMENT_MANAGEMENT', 'COMMENT_DELETE', '/api/v1/comments/*', 'DELETE', 'Delete comment'),
('Moderate Comment', 'COMMENT_MANAGEMENT', 'COMMENT_MODERATE', '/api/v1/comments/*/moderate', 'POST', 'Moderate comments');

-- Payment Permissions
INSERT INTO permissions (name, category, slug, api_url, api_method, description) VALUES
('Read Payment', 'PAYMENT', 'PAYMENT_READ', '/api/v1/payments/*', 'GET', 'View payment information'),
('Create Payment', 'PAYMENT', 'PAYMENT_WRITE', '/api/v1/payments', 'POST', 'Create payment'),
('Process Payment', 'PAYMENT', 'PAYMENT_PROCESS', '/api/v1/payments/*/process', 'POST', 'Process payment transaction'),
('Refund Payment', 'PAYMENT', 'PAYMENT_REFUND', '/api/v1/payments/*/refund', 'POST', 'Refund payment');

-- Admin Permissions
INSERT INTO permissions (name, category, slug, api_url, api_method, description) VALUES
('Admin Read', 'ADMIN', 'ADMIN_READ', '/api/v1/admin/*', 'GET', 'Admin read access'),
('Admin Write', 'ADMIN', 'ADMIN_WRITE', '/api/v1/admin/*', 'POST', 'Admin write access'),
('Admin Delete', 'ADMIN', 'ADMIN_DELETE', '/api/v1/admin/*', 'DELETE', 'Admin delete access'),
('System Config', 'ADMIN', 'SYSTEM_CONFIG', '/api/v1/admin/config', 'PUT', 'Modify system configuration');

-- Analytics Permissions
INSERT INTO permissions (name, category, slug, api_url, api_method, description) VALUES
('Read Analytics', 'ANALYTICS', 'ANALYTICS_READ', '/api/v1/analytics/*', 'GET', 'View analytics data'),
('Write Analytics', 'ANALYTICS', 'ANALYTICS_WRITE', '/api/v1/analytics/*', 'POST', 'Create analytics data');

-- ============================================
-- 3. ASSIGN PERMISSIONS TO ROLES
-- ============================================

-- GUEST Role Permissions (Read-only)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'GUEST' AND p.slug IN (
    'POST_READ',
    'COMMENT_READ'
);

-- USER Role Permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'USER' AND p.slug IN (
    'USER_READ',
    'USER_UPDATE',
    'POST_READ',
    'COMMENT_READ',
    'COMMENT_WRITE',
    'PAYMENT_READ'
);

-- AUTHOR Role Permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'AUTHOR' AND p.slug IN (
    'USER_READ',
    'USER_UPDATE',
    'POST_READ',
    'POST_WRITE',
    'POST_UPDATE',
    'COMMENT_READ',
    'COMMENT_WRITE',
    'COMMENT_MODERATE',
    'PAYMENT_READ',
    'ANALYTICS_READ'
);

-- EDITOR Role Permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'EDITOR' AND p.slug IN (
    'USER_READ',
    'USER_UPDATE',
    'POST_READ',
    'POST_WRITE',
    'POST_UPDATE',
    'POST_DELETE',
    'POST_PUBLISH',
    'COMMENT_READ',
    'COMMENT_WRITE',
    'COMMENT_DELETE',
    'COMMENT_MODERATE',
    'PAYMENT_READ',
    'ANALYTICS_READ',
    'ANALYTICS_WRITE'
);

-- MODERATOR Role Permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'MODERATOR' AND p.slug IN (
    'USER_READ',
    'POST_READ',
    'POST_UPDATE',
    'COMMENT_READ',
    'COMMENT_DELETE',
    'COMMENT_MODERATE',
    'ANALYTICS_READ'
);

-- ADMIN Role Permissions (All permissions)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- ============================================
-- 4. CREATE DEFAULT ADMIN USER (Optional)
-- ============================================
-- Password: admin123 (BCrypt encrypted)
-- You should change this password after first login!

INSERT INTO users (username, email, password) VALUES
('admin', 'admin@blogging-platform.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');

-- Assign ADMIN role to admin user
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- ============================================
-- 5. VERIFICATION QUERIES
-- ============================================

-- Check roles
SELECT * FROM roles ORDER BY name;

-- Check permissions by category
SELECT category, COUNT(*) as permission_count 
FROM permissions 
GROUP BY category 
ORDER BY category;

-- Check role-permission mappings
SELECT r.name as role_name, COUNT(rp.permission_id) as permission_count
FROM roles r
LEFT JOIN role_permissions rp ON r.id = rp.role_id
GROUP BY r.name
ORDER BY r.name;

-- Check admin user
SELECT u.username, u.email, r.name as role
FROM users u
JOIN users_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin';

-- ============================================
-- NOTES:
-- ============================================
-- 1. The ADMIN role has is_full_access = true, meaning it bypasses permission checks
-- 2. Permission slugs are used as authorities in Spring Security
-- 3. Role names are prefixed with "ROLE_" automatically by Spring Security
-- 4. Users can have both role-based and direct permissions
-- 5. Remember to change the default admin password!
