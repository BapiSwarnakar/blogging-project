# Database-Driven Security System - Implementation Guide

## 🎯 Overview

Your blogging platform uses a **database-driven role and permission system** managed by the `authentication-service`. This provides dynamic, flexible access control that can be managed without code changes.

## 📊 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                  Authentication Service                      │
│  ┌────────────┐  ┌────────────┐  ┌─────────────────┐       │
│  │   Users    │  │   Roles    │  │   Permissions   │       │
│  │            │  │            │  │                 │       │
│  │ - username │  │ - name     │  │ - name          │       │
│  │ - password │  │ - desc     │  │ - category      │       │
│  │ - email    │  │ - isActive │  │ - slug ⭐       │       │
│  └────────────┘  └────────────┘  │ - apiUrl        │       │
│         │              │          │ - apiMethod     │       │
│         └──────┬───────┘          └─────────────────┘       │
│                │                           │                 │
│         ┌──────▼──────┐           ┌───────▼────────┐       │
│         │ users_roles │           │role_permissions│       │
│         │  (M-to-M)   │           │   (M-to-M)     │       │
│         └─────────────┘           └────────────────┘       │
│                                                              │
│         ┌─────────────────┐                                 │
│         │users_permissions│  (Direct user permissions)      │
│         │    (M-to-M)     │                                 │
│         └─────────────────┘                                 │
└─────────────────────────────────────────────────────────────┘
                          │
                          │ JWT Token with authorities
                          ▼
┌─────────────────────────────────────────────────────────────┐
│              Other Microservices                             │
│  (user-management, payment-service, etc.)                   │
│                                                              │
│  ┌──────────────────────────────────────────┐              │
│  │  CommonJwtAuthenticationFilter           │              │
│  │  - Validates JWT                         │              │
│  │  - Extracts authorities from token       │              │
│  │  - Sets SecurityContext                  │              │
│  └──────────────────────────────────────────┘              │
└─────────────────────────────────────────────────────────────┘
```

## 🔑 Key Concepts

### 1. Permission Slug ⭐
The **`slug`** field in the `permissions` table is the most important field. It's used as the **authority** in Spring Security.

**Example:**
```sql
INSERT INTO permissions (name, category, slug, ...) VALUES
('Read User', 'USER_MANAGEMENT', 'USER_READ', ...);
```

In code:
```java
@PreAuthorize("hasAuthority('USER_READ')")  // ← Uses the slug
```

### 2. Role Name
The **`name`** field in the `roles` table is used for role-based checks.

**Example:**
```sql
INSERT INTO roles (name, description, ...) VALUES
('ADMIN', 'System Administrator', ...);
```

In code:
```java
@PreAuthorize("hasRole('ADMIN')")  // ← Spring adds ROLE_ prefix automatically
```

### 3. Authority Format in JWT
When a user logs in, the JWT token contains all their authorities:
```
"auth": "ROLE_ADMIN,USER_READ,USER_WRITE,POST_READ,POST_WRITE,..."
```

This includes:
- **Roles**: Prefixed with `ROLE_` (e.g., `ROLE_ADMIN`, `ROLE_USER`)
- **Permissions**: Permission slugs (e.g., `USER_READ`, `POST_WRITE`)

## 📁 Database Schema

### Tables

#### `users`
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| username | VARCHAR | Unique username |
| password | VARCHAR | BCrypt encrypted password |
| email | VARCHAR | Unique email |

#### `roles`
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| name | VARCHAR | Role name (e.g., "ADMIN") |
| description | VARCHAR | Role description |
| is_active | BOOLEAN | Active status |
| is_full_access | BOOLEAN | Bypass permission checks |

#### `permissions`
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| name | VARCHAR | Display name |
| category | VARCHAR | Permission category |
| **slug** | VARCHAR | **Authority slug (UNIQUE)** ⭐ |
| api_url | VARCHAR | Associated API endpoint |
| api_method | VARCHAR | HTTP method |
| description | VARCHAR | Permission description |

#### `users_roles` (Many-to-Many)
| Column | Type |
|--------|------|
| user_id | BIGINT |
| role_id | BIGINT |

#### `role_permissions` (Many-to-Many)
| Column | Type |
|--------|------|
| role_id | BIGINT |
| permission_id | BIGINT |

#### `users_permissions` (Many-to-Many - Direct Permissions)
| Column | Type |
|--------|------|
| user_id | BIGINT |
| permission_id | BIGINT |

## 🚀 Setup Instructions

### Step 1: Initialize Database

Run the initialization script:
```bash
# Navigate to authentication-service resources
cd authentication-service/src/main/resources/db/

# Run the SQL script
mysql -u your_user -p your_database < init-roles-permissions.sql
```

This creates:
- ✅ 6 default roles (ADMIN, USER, AUTHOR, EDITOR, MODERATOR, GUEST)
- ✅ 26 permissions across 6 categories
- ✅ Role-permission mappings
- ✅ Default admin user (username: `admin`, password: `admin123`)

### Step 2: Verify Setup

```sql
-- Check roles
SELECT * FROM roles;

-- Check permissions
SELECT * FROM permissions ORDER BY category, slug;

-- Check role permissions
SELECT r.name, p.slug 
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
ORDER BY r.name, p.slug;
```

### Step 3: Test Authentication

```bash
# Login as admin
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Response includes JWT token
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "username": "admin",
  "email": "admin@blogging-platform.com"
}
```

### Step 4: Use JWT Token

```bash
# Access protected endpoint
curl -X GET http://localhost:9092/api/v1/users/me \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 💻 Usage Examples

### In Controllers

```java
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    
    // Check permission by slug
    @PreAuthorize("hasAuthority('POST_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }
    
    // Check role
    @PreAuthorize("hasRole('AUTHOR')")
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDTO dto) {
        return ResponseEntity.ok(postService.createPost(dto));
    }
    
    // Check multiple permissions (OR)
    @PreAuthorize("hasAnyAuthority('POST_PUBLISH', 'ADMIN_WRITE')")
    @PostMapping("/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.publishPost(id));
    }
    
    // Complex expression
    @PreAuthorize("hasRole('ADMIN') or (hasAuthority('POST_UPDATE') and #authorUsername == authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
        @PathVariable Long id, 
        @RequestParam String authorUsername,
        @RequestBody PostDTO dto
    ) {
        return ResponseEntity.ok(postService.updatePost(id, dto));
    }
}
```

### In Services

```java
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    
    public void deletePost(Long postId) {
        String currentUser = SecurityUtils.getCurrentUsername();
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        // Check if user has permission
        boolean canDelete = SecurityUtils.hasPermission("POST_DELETE");
        boolean isAdmin = SecurityUtils.hasRole("ROLE_ADMIN");
        boolean isOwner = post.getAuthor().equals(currentUser);
        
        if (!canDelete && !isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }
        
        postRepository.delete(post);
    }
    
    public List<Post> getPosts() {
        if (SecurityUtils.hasRole("ROLE_ADMIN")) {
            // Admin sees all posts
            return postRepository.findAll();
        } else if (SecurityUtils.hasAuthority("POST_READ")) {
            // Regular users see only published posts
            return postRepository.findByStatus("PUBLISHED");
        } else {
            throw new AccessDeniedException("No permission to view posts");
        }
    }
}
```

## 🔧 Managing Roles and Permissions

### Adding a New Permission

```java
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    public PermissionEntity createPermission(PermissionDTO dto) {
        PermissionEntity permission = PermissionEntity.builder()
            .name(dto.getName())
            .category(dto.getCategory())
            .slug(dto.getSlug())  // e.g., "REPORT_GENERATE"
            .apiUrl(dto.getApiUrl())
            .apiMethod(dto.getApiMethod())
            .description(dto.getDescription())
            .build();
        
        return permissionRepository.save(permission);
    }
}
```

### Assigning Permission to Role

```java
@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    @Transactional
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        RoleEntity role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        PermissionEntity permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        
        role.addPermission(permission);
        roleRepository.save(role);
    }
}
```

### Assigning Role to User

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        RoleEntity role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        
        user.addRole(role);
        userRepository.save(user);
    }
}
```

### Granting Direct Permission to User

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    @Transactional
    public void grantDirectPermission(Long userId, Long permissionId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        PermissionEntity permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        
        user.addDirectPermission(permission);
        userRepository.save(user);
    }
}
```

## 📋 Permission Naming Convention

### Format: `{RESOURCE}_{ACTION}`

**Resources:**
- USER
- POST
- COMMENT
- PAYMENT
- ADMIN
- ANALYTICS
- REPORT
- CATEGORY
- TAG

**Actions:**
- READ
- WRITE (Create)
- UPDATE
- DELETE
- PUBLISH
- MODERATE
- PROCESS
- REFUND
- EXPORT
- IMPORT

**Examples:**
- `USER_READ`
- `POST_WRITE`
- `COMMENT_MODERATE`
- `PAYMENT_PROCESS`
- `REPORT_EXPORT`

## 🎯 Best Practices

### 1. Use Permission Slugs in Code
✅ **Do:**
```java
@PreAuthorize("hasAuthority('USER_READ')")
```

❌ **Don't:**
```java
@PreAuthorize("hasAuthority(T(com.stech.common.security.Permission).USER_READ)")
```

### 2. Keep Slugs Consistent
- Use UPPERCASE
- Use underscores
- Follow RESOURCE_ACTION pattern

### 3. Document API Endpoints
Always fill in `api_url` and `api_method` in permissions table for documentation.

### 4. Use Categories
Group related permissions using the `category` field:
- USER_MANAGEMENT
- POST_MANAGEMENT
- COMMENT_MANAGEMENT
- PAYMENT
- ADMIN
- ANALYTICS

### 5. Leverage Direct Permissions
Use direct user permissions for:
- Temporary access
- Special cases
- Testing
- Emergency access

## 🔐 Security Considerations

### 1. Full Access Role
The `is_full_access` flag on roles can bypass permission checks. Use carefully!

```java
if (role.isFullAccess()) {
    // Grant all access
    return true;
}
```

### 2. Password Security
- Always use BCrypt for password encryption
- Minimum password length: 8 characters
- Change default admin password immediately

### 3. JWT Token Security
- Keep JWT secret key secure
- Use appropriate expiration time
- Rotate secrets periodically

### 4. Permission Granularity
- Don't make permissions too fine-grained
- Balance between flexibility and complexity
- Group related actions when possible

## 📊 Monitoring and Auditing

### Track Permission Usage

```sql
-- Most used permissions
SELECT p.slug, p.name, COUNT(rp.role_id) as role_count
FROM permissions p
LEFT JOIN role_permissions rp ON p.id = rp.permission_id
GROUP BY p.id
ORDER BY role_count DESC;

-- Users with specific permission
SELECT DISTINCT u.username, u.email
FROM users u
LEFT JOIN users_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
LEFT JOIN role_permissions rp ON r.id = rp.role_id
LEFT JOIN permissions p ON rp.permission_id = p.id
WHERE p.slug = 'ADMIN_DELETE';
```

## 🚀 Next Steps

1. ✅ **Initialize database** with provided SQL script
2. ✅ **Test authentication** with default admin user
3. ✅ **Create additional users** with different roles
4. ✅ **Test authorization** on protected endpoints
5. ⏳ **Build admin panel** for managing roles/permissions
6. ⏳ **Add audit logging** for permission changes
7. ⏳ **Implement permission caching** for performance

---

**Version**: 1.0  
**Last Updated**: December 7, 2025  
**Approach**: Database-Driven with Dynamic Role-Permission Management
