# Security Quick Reference Guide

## 🔐 Common Imports

```java
import com.stech.common.security.Role;
import com.stech.common.security.Permission;
import com.stech.common.security.util.SecurityUtils;
import com.stech.common.security.filter.CommonJwtAuthenticationFilter;
import com.stech.common.security.config.BaseSecurityConfig;
```

## 📋 Available Roles

```java
Role.ROLE_USER          // Regular users
Role.ROLE_ADMIN         // Full access administrators
Role.ROLE_MODERATOR     // Content moderators
Role.ROLE_AUTHOR        // Blog post authors
Role.ROLE_EDITOR        // Blog editors (can publish)
Role.ROLE_SYSTEM        // Inter-service communication
Role.ROLE_GUEST         // Limited read-only access
```

## 🔑 Available Permissions

### User Permissions
```java
Permission.USER_READ
Permission.USER_WRITE
Permission.USER_UPDATE
Permission.USER_DELETE
```

### Post Permissions
```java
Permission.POST_READ
Permission.POST_WRITE
Permission.POST_UPDATE
Permission.POST_DELETE
Permission.POST_PUBLISH
```

### Comment Permissions
```java
Permission.COMMENT_READ
Permission.COMMENT_WRITE
Permission.COMMENT_DELETE
Permission.COMMENT_MODERATE
```

### Payment Permissions
```java
Permission.PAYMENT_READ
Permission.PAYMENT_WRITE
Permission.PAYMENT_PROCESS
Permission.PAYMENT_REFUND
```

### Admin Permissions
```java
Permission.ADMIN_READ
Permission.ADMIN_WRITE
Permission.ADMIN_DELETE
Permission.SYSTEM_CONFIG
```

### Analytics Permissions
```java
Permission.ANALYTICS_READ
Permission.ANALYTICS_WRITE
```

## 🛡️ Controller Security Annotations

### Single Permission
```java
@PreAuthorize("hasAuthority(T(com.stech.common.security.Permission).USER_READ)")
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) { }
```

### Multiple Permissions (OR)
```java
@PreAuthorize("hasAnyAuthority(" +
    "T(com.stech.common.security.Permission).USER_WRITE, " +
    "T(com.stech.common.security.Permission).ADMIN_WRITE)")
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) { }
```

### Role Check
```java
@PreAuthorize("hasRole(T(com.stech.common.security.Role).ROLE_ADMIN.getRoleName())")
@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) { }
```

### Multiple Roles (OR)
```java
@PreAuthorize("hasAnyRole(" +
    "T(com.stech.common.security.Role).ROLE_ADMIN.getRoleName(), " +
    "T(com.stech.common.security.Role).ROLE_MODERATOR.getRoleName())")
@PutMapping("/posts/{id}/moderate")
public ResponseEntity<Post> moderatePost(@PathVariable Long id) { }
```

### Complex Expression
```java
@PreAuthorize("hasRole(T(com.stech.common.security.Role).ROLE_ADMIN.getRoleName()) " +
    "or (hasAuthority(T(com.stech.common.security.Permission).POST_UPDATE) " +
    "and #username == authentication.name)")
@PutMapping("/posts/{id}")
public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestParam String username) { }
```

## 🔧 SecurityUtils Methods

### Get Current User
```java
String username = SecurityUtils.getCurrentUsername();
List<String> authorities = SecurityUtils.getCurrentAuthorities();
List<String> permissions = SecurityUtils.getCurrentUserPermissions();
```

### Check Authentication
```java
boolean isAuth = SecurityUtils.isAuthenticated();
boolean isAdmin = SecurityUtils.isAdmin();
```

### Check Roles
```java
// Single role
boolean hasRole = SecurityUtils.hasRole(Role.ROLE_ADMIN);
boolean hasRoleByName = SecurityUtils.hasRole("ROLE_ADMIN");

// Any role
boolean hasAny = SecurityUtils.hasAnyRole(Role.ROLE_ADMIN, Role.ROLE_MODERATOR);

// All roles
boolean hasAll = SecurityUtils.hasAllRoles(Role.ROLE_ADMIN, Role.ROLE_SYSTEM);
```

### Check Permissions
```java
// Single permission
boolean hasPerm = SecurityUtils.hasPermission(Permission.USER_WRITE);

// Any permission
boolean hasAnyPerm = SecurityUtils.hasAnyPermission(
    Permission.USER_WRITE, 
    Permission.ADMIN_WRITE
);

// All permissions
boolean hasAllPerms = SecurityUtils.hasAllPermissions(
    Permission.POST_READ, 
    Permission.POST_WRITE
);
```

## ⚙️ Service Configuration Template

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends BaseSecurityConfig {
    
    @Bean
    public CommonJwtAuthenticationFilter jwtAuthenticationFilter() {
        // Add service-specific public URLs
        String[] servicePublicUrls = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/public/**"
        };
        return new CommonJwtAuthenticationFilter(servicePublicUrls);
    }
    
    @Override
    protected CommonJwtAuthenticationFilter getJwtAuthenticationFilter() {
        return jwtAuthenticationFilter();
    }
    
    @Override
    protected String[] getServiceSpecificPublicPaths() {
        return new String[]{
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/public/**"
        };
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return configureSecurityFilterChain(http);
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return super.corsConfigurationSource();
    }
}
```

## 🎯 Common Use Cases

### 1. User Can Only Update Their Own Profile
```java
@Service
public class UserService {
    public void updateProfile(Long userId, UserDTO dto) {
        String currentUser = SecurityUtils.getCurrentUsername();
        User user = userRepository.findById(userId).orElseThrow();
        
        if (!user.getUsername().equals(currentUser) && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Can only update your own profile");
        }
        
        // Update logic
    }
}
```

### 2. Author Can Delete Own Posts, Admin Can Delete Any
```java
@Service
public class PostService {
    public void deletePost(Long postId) {
        String currentUser = SecurityUtils.getCurrentUsername();
        Post post = postRepository.findById(postId).orElseThrow();
        
        boolean isOwner = post.getAuthor().equals(currentUser);
        boolean isAdmin = SecurityUtils.isAdmin();
        
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Cannot delete this post");
        }
        
        postRepository.delete(post);
    }
}
```

### 3. Check Permission Before Action
```java
@Service
public class PaymentService {
    public void processRefund(Long paymentId) {
        if (!SecurityUtils.hasPermission(Permission.PAYMENT_REFUND)) {
            throw new AccessDeniedException("No permission to process refunds");
        }
        
        // Refund logic
    }
}
```

### 4. Different Logic Based on Role
```java
@Service
public class AnalyticsService {
    public AnalyticsDTO getAnalytics() {
        if (SecurityUtils.isAdmin()) {
            // Return full analytics
            return analyticsRepository.getFullAnalytics();
        } else if (SecurityUtils.hasRole(Role.ROLE_AUTHOR)) {
            // Return only author's analytics
            String username = SecurityUtils.getCurrentUsername();
            return analyticsRepository.getAuthorAnalytics(username);
        } else {
            // Return limited analytics
            return analyticsRepository.getPublicAnalytics();
        }
    }
}
```

## 📊 Permission Matrix Quick Reference

| Action | Required Permission | Allowed Roles |
|--------|-------------------|---------------|
| View user profile | USER_READ | USER, AUTHOR, EDITOR, ADMIN |
| Edit own profile | USER_UPDATE | USER, AUTHOR, EDITOR, ADMIN |
| Delete user | USER_DELETE | ADMIN |
| Create post | POST_WRITE | AUTHOR, EDITOR, ADMIN |
| Publish post | POST_PUBLISH | EDITOR, ADMIN |
| Delete any post | POST_DELETE | EDITOR, ADMIN |
| Moderate comments | COMMENT_MODERATE | AUTHOR, EDITOR, MODERATOR, ADMIN |
| Process payment | PAYMENT_PROCESS | ADMIN |
| View analytics | ANALYTICS_READ | AUTHOR, EDITOR, ADMIN |

## 🚨 Common Mistakes to Avoid

❌ **Don't use hardcoded strings**
```java
@PreAuthorize("hasAuthority('USER_READ')")  // BAD
```

✅ **Use constants**
```java
@PreAuthorize("hasAuthority(T(com.stech.common.security.Permission).USER_READ)")  // GOOD
```

❌ **Don't duplicate security logic**
```java
// BAD - duplicating filter in each service
public class MyJwtFilter extends OncePerRequestFilter { }
```

✅ **Use centralized filter**
```java
// GOOD - use common filter
@Bean
public CommonJwtAuthenticationFilter jwtFilter() {
    return new CommonJwtAuthenticationFilter(publicUrls);
}
```

❌ **Don't check roles with strings**
```java
if (authorities.contains("ROLE_ADMIN")) { }  // BAD
```

✅ **Use SecurityUtils**
```java
if (SecurityUtils.isAdmin()) { }  // GOOD
```

## 📞 Need Help?

- **Documentation**: See `SECURITY_README.md` for detailed guide
- **Examples**: Check `SECURITY_IMPLEMENTATION_SUMMARY.md`
- **Source Code**: `common-util/src/main/java/com/stech/common/security/`

---
**Quick Reference Version**: 1.0  
**Last Updated**: December 7, 2025
