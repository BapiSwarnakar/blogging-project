# Database-Driven Security System - Updated Approach

## Overview
The authentication-service already has a robust database-driven role and permission system. The common security module has been updated to work seamlessly with this existing system.

## Database Schema

### Tables
1. **`users`** - User accounts
2. **`roles`** - Roles (e.g., ADMIN, USER, AUTHOR)
3. **`permissions`** - Permissions with slug (e.g., USER_READ, POST_WRITE)
4. **`users_roles`** - User-Role mapping (Many-to-Many)
5. **`role_permissions`** - Role-Permission mapping (Many-to-Many)
6. **`users_permissions`** - Direct user permissions (Many-to-Many)

### Entity Structure

#### RoleEntity
- `id` - Primary key
- `name` - Role name (e.g., "ADMIN", "USER")
- `description` - Role description
- `isActive` - Active status
- `isFullAccess` - Full access flag
- `permissions` - Set of permissions (Many-to-Many)
- `users` - Set of users (Many-to-Many)

#### PermissionEntity
- `id` - Primary key
- `name` - Permission display name
- `category` - Permission category
- `slug` - Unique slug (e.g., "USER_READ") - **Used as authority**
- `apiUrl` - Associated API URL
- `apiMethod` - HTTP method
- `description` - Permission description
- `roles` - Set of roles (Many-to-Many)
- `users` - Set of users with direct permission (Many-to-Many)

#### UserEntity
- `id` - Primary key
- `username` - Unique username
- `password` - Encrypted password
- `email` - Unique email
- `roles` - Set of roles (Many-to-Many)
- `directPermissions` - Direct permissions (Many-to-Many)

## How It Works

### 1. User Authentication
```
User Login → CustomUserDetailsServiceImpl loads user with roles & permissions
          → CustomUserDetails converts to GrantedAuthorities
          → Roles: "ROLE_" + roleName.toUpperCase()
          → Permissions: permission.slug
```

### 2. JWT Token Generation
```
Authentication → JwtTokenProvider.generateToken()
              → Extracts all authorities (roles + permissions)
              → Stores in JWT claim "auth" as comma-separated string
              → Example: "ROLE_ADMIN,USER_READ,USER_WRITE,POST_READ"
```

### 3. JWT Token Validation (Other Services)
```
JWT Token → CommonJwtAuthenticationFilter extracts token
         → JwtTokenLibrary.validateToken() validates
         → JwtTokenLibrary.getAuthorities() extracts authorities
         → Sets SecurityContext with authorities
```

## Using the System

### In Controllers - Use Permission Slugs

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    // Check permission by slug
    @PreAuthorize("hasAuthority('USER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }
    
    // Check role (with ROLE_ prefix)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Check multiple permissions
    @PreAuthorize("hasAnyAuthority('USER_WRITE', 'ADMIN_WRITE')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }
}
```

### In Services - Use SecurityUtils

```java
@Service
public class PostService {
    
    public void deletePost(Long postId) {
        String currentUser = SecurityUtils.getCurrentUsername();
        Post post = postRepository.findById(postId).orElseThrow();
        
        // Check permission by slug
        boolean canDelete = SecurityUtils.hasPermission("POST_DELETE");
        
        // Or check role
        boolean isAdmin = SecurityUtils.hasRole("ROLE_ADMIN");
        
        if (!canDelete && !isAdmin && !post.getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("Cannot delete this post");
        }
        
        postRepository.delete(post);
    }
}
```

## Permission Slug Naming Convention

Based on the database structure, permission slugs should follow this pattern:

### Format: `{RESOURCE}_{ACTION}`

Examples:
- `USER_READ` - Read user data
- `USER_WRITE` - Create user
- `USER_UPDATE` - Update user
- `USER_DELETE` - Delete user
- `POST_READ` - Read posts
- `POST_WRITE` - Create posts
- `POST_UPDATE` - Update posts
- `POST_DELETE` - Delete posts
- `POST_PUBLISH` - Publish posts
- `COMMENT_READ` - Read comments
- `COMMENT_WRITE` - Create comments
- `COMMENT_DELETE` - Delete comments
- `COMMENT_MODERATE` - Moderate comments
- `PAYMENT_READ` - View payments
- `PAYMENT_WRITE` - Create payment
- `PAYMENT_PROCESS` - Process payment
- `PAYMENT_REFUND` - Refund payment
- `ADMIN_READ` - Admin read access
- `ADMIN_WRITE` - Admin write access
- `ADMIN_DELETE` - Admin delete access
- `SYSTEM_CONFIG` - System configuration

## Managing Roles and Permissions

### Creating Roles (via authentication-service)

```java
@Service
public class RoleService {
    
    public RoleEntity createRole(String name, String description) {
        RoleEntity role = RoleEntity.builder()
            .name(name)
            .description(description)
            .isActive(true)
            .isFullAccess(false)
            .build();
        return roleRepository.save(role);
    }
}
```

### Creating Permissions

```java
@Service
public class PermissionService {
    
    public PermissionEntity createPermission(
        String name, 
        String category, 
        String slug,
        String apiUrl,
        String apiMethod
    ) {
        PermissionEntity permission = PermissionEntity.builder()
            .name(name)
            .category(category)
            .slug(slug)
            .apiUrl(apiUrl)
            .apiMethod(apiMethod)
            .build();
        return permissionRepository.save(permission);
    }
}
```

### Assigning Permissions to Roles

```java
@Service
public class RoleService {
    
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        RoleEntity role = roleRepository.findById(roleId).orElseThrow();
        PermissionEntity permission = permissionRepository.findById(permissionId).orElseThrow();
        
        role.addPermission(permission);
        roleRepository.save(role);
    }
}
```

### Assigning Roles to Users

```java
@Service
public class UserService {
    
    public void assignRoleToUser(Long userId, Long roleId) {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        RoleEntity role = roleRepository.findById(roleId).orElseThrow();
        
        user.addRole(role);
        userRepository.save(user);
    }
}
```

## Common Security Module Integration

The common security module provides:

1. **CommonJwtAuthenticationFilter** - Validates JWT and extracts authorities
2. **SecurityUtils** - Helper methods for security checks
3. **BaseSecurityConfig** - Base security configuration

### Key Points:

- ✅ **No static roles/permissions** - All loaded from database
- ✅ **Permission slugs** - Used as authorities in @PreAuthorize
- ✅ **Role names** - Prefixed with "ROLE_" automatically
- ✅ **JWT contains all authorities** - Roles + Permissions
- ✅ **Works across all services** - JWT validated consistently

## Example: Complete Flow

### 1. User Registration (authentication-service)
```
POST /api/v1/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}

→ Creates user in database
→ Assigns default "USER" role
→ USER role has permissions: USER_READ, USER_UPDATE, POST_READ, COMMENT_READ
```

### 2. User Login (authentication-service)
```
POST /api/v1/auth/login
{
  "username": "john",
  "password": "password123"
}

→ Authenticates user
→ Loads roles and permissions from DB
→ Generates JWT with authorities: "ROLE_USER,USER_READ,USER_UPDATE,POST_READ,COMMENT_READ"
→ Returns JWT token
```

### 3. Access Protected Resource (user-management service)
```
GET /api/v1/users/me
Headers: Authorization: Bearer <JWT>

→ CommonJwtAuthenticationFilter validates JWT
→ Extracts authorities from token
→ Sets SecurityContext
→ @PreAuthorize("hasAuthority('USER_READ')") checks permission
→ Returns user data
```

## Migration from Static to Database-Driven

The static `Role` and `Permission` classes in common-util are now **reference/documentation only**. They show the recommended permission slugs but are not used for authorization.

### What to Use:

❌ **Don't use**: `T(com.stech.common.security.Permission).USER_READ`  
✅ **Use**: `'USER_READ'` (the actual slug from database)

❌ **Don't use**: `T(com.stech.common.security.Role).ROLE_ADMIN`  
✅ **Use**: `'ADMIN'` (role name from database, Spring adds ROLE_ prefix)

### Updated Controller Example:

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    // Use permission slug directly
    @PreAuthorize("hasAuthority('USER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) { }
    
    // Use role name (without ROLE_ prefix)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) { }
}
```

## Benefits of Database-Driven Approach

1. **Dynamic** - Add/remove roles and permissions without code changes
2. **Flexible** - Assign permissions to roles via admin panel
3. **Granular** - Direct user permissions for special cases
4. **Auditable** - Track permission changes in database
5. **Scalable** - Easy to add new permissions as features grow
6. **Manageable** - Non-developers can manage permissions via UI

## Recommended Setup

### Initial Roles:
1. **ADMIN** - Full system access (isFullAccess = true)
2. **USER** - Basic user permissions
3. **AUTHOR** - Can create and manage own posts
4. **EDITOR** - Can publish and edit all posts
5. **MODERATOR** - Can moderate content

### Initial Permissions:
Create permissions for each resource-action combination needed by your application.

---

**Updated**: December 7, 2025  
**Approach**: Database-Driven with JWT Token Authority Storage
