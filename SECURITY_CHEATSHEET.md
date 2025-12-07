# 🚀 Security Quick Reference Card

## 📝 Controller Annotations

### Check Permission
```java
@PreAuthorize("hasAuthority('USER_READ')")
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) { }
```

### Check Role
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) { }
```

### Check Multiple Permissions (OR)
```java
@PreAuthorize("hasAnyAuthority('USER_WRITE', 'ADMIN_WRITE')")
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody UserDTO dto) { }
```

### Check Multiple Roles (OR)
```java
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@PutMapping("/posts/{id}/moderate")
public ResponseEntity<Post> moderate(@PathVariable Long id) { }
```

### Complex Expression
```java
@PreAuthorize("hasRole('ADMIN') or (hasAuthority('POST_UPDATE') and #author == authentication.name)")
@PutMapping("/posts/{id}")
public ResponseEntity<Post> update(@PathVariable Long id, @RequestParam String author) { }
```

---

## 🔧 SecurityUtils Methods

### Get Current User
```java
String username = SecurityUtils.getCurrentUsername();
```

### Check Authentication
```java
boolean isAuth = SecurityUtils.isAuthenticated();
boolean isAdmin = SecurityUtils.isAdmin();
```

### Check Role
```java
boolean hasRole = SecurityUtils.hasRole("ADMIN");
boolean hasAny = SecurityUtils.hasAnyRole("ADMIN", "MODERATOR");
boolean hasAll = SecurityUtils.hasAllRoles("ADMIN", "SYSTEM");
```

### Check Permission
```java
boolean hasPerm = SecurityUtils.hasPermission("USER_READ");
boolean hasAny = SecurityUtils.hasAnyPermission("USER_WRITE", "ADMIN_WRITE");
boolean hasAll = SecurityUtils.hasAllPermissions("POST_READ", "POST_WRITE");
```

### Get Authorities
```java
List<String> all = SecurityUtils.getCurrentAuthorities();
List<String> roles = SecurityUtils.getCurrentRoles();
List<String> perms = SecurityUtils.getCurrentPermissions();
```

---

## 🗄️ Database Queries

### Get User's Roles
```sql
SELECT r.name FROM roles r
JOIN users_roles ur ON r.id = ur.role_id
WHERE ur.user_id = ?;
```

### Get User's Permissions (via roles)
```sql
SELECT DISTINCT p.slug FROM permissions p
JOIN role_permissions rp ON p.id = rp.permission_id
JOIN users_roles ur ON rp.role_id = ur.role_id
WHERE ur.user_id = ?;
```

### Get Role's Permissions
```sql
SELECT p.slug FROM permissions p
JOIN role_permissions rp ON p.id = rp.permission_id
WHERE rp.role_id = ?;
```

### Add Permission to Role
```sql
INSERT INTO role_permissions (role_id, permission_id)
VALUES (?, ?);
```

### Assign Role to User
```sql
INSERT INTO users_roles (user_id, role_id)
VALUES (?, ?);
```

---

## 📋 Permission Slugs Reference

### User Management
- `USER_READ` - View users
- `USER_WRITE` - Create users
- `USER_UPDATE` - Update users
- `USER_DELETE` - Delete users

### Post Management
- `POST_READ` - View posts
- `POST_WRITE` - Create posts
- `POST_UPDATE` - Update posts
- `POST_DELETE` - Delete posts
- `POST_PUBLISH` - Publish posts

### Comment Management
- `COMMENT_READ` - View comments
- `COMMENT_WRITE` - Create comments
- `COMMENT_DELETE` - Delete comments
- `COMMENT_MODERATE` - Moderate comments

### Payment
- `PAYMENT_READ` - View payments
- `PAYMENT_WRITE` - Create payments
- `PAYMENT_PROCESS` - Process payments
- `PAYMENT_REFUND` - Refund payments

### Admin
- `ADMIN_READ` - Admin read access
- `ADMIN_WRITE` - Admin write access
- `ADMIN_DELETE` - Admin delete access
- `SYSTEM_CONFIG` - System configuration

### Analytics
- `ANALYTICS_READ` - View analytics
- `ANALYTICS_WRITE` - Create analytics

---

## 🎭 Role Names Reference

- `ADMIN` - Full system access
- `USER` - Basic user
- `AUTHOR` - Blog author
- `EDITOR` - Blog editor (can publish)
- `MODERATOR` - Content moderator
- `GUEST` - Read-only access
- `SYSTEM` - Inter-service communication

---

## 🔐 JWT Token Structure

```json
{
  "sub": "username",
  "auth": "ROLE_ADMIN,USER_READ,USER_WRITE,POST_READ,...",
  "iat": 1733577000,
  "exp": 1733663400
}
```

---

## 📖 Common Use Cases

### User Can Only Update Own Profile
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

### Author Can Delete Own Posts, Admin Can Delete Any
```java
@Service
public class PostService {
    public void deletePost(Long postId) {
        String currentUser = SecurityUtils.getCurrentUsername();
        Post post = postRepository.findById(postId).orElseThrow();
        
        boolean isOwner = post.getAuthor().equals(currentUser);
        boolean canDelete = SecurityUtils.hasPermission("POST_DELETE");
        boolean isAdmin = SecurityUtils.isAdmin();
        
        if (!isOwner && !canDelete && !isAdmin) {
            throw new AccessDeniedException("Cannot delete this post");
        }
        postRepository.delete(post);
    }
}
```

### Different Logic Based on Role
```java
@Service
public class AnalyticsService {
    public AnalyticsDTO getAnalytics() {
        if (SecurityUtils.isAdmin()) {
            return analyticsRepository.getFullAnalytics();
        } else if (SecurityUtils.hasRole("AUTHOR")) {
            String username = SecurityUtils.getCurrentUsername();
            return analyticsRepository.getAuthorAnalytics(username);
        } else {
            return analyticsRepository.getPublicAnalytics();
        }
    }
}
```

---

## ⚠️ Common Mistakes

### ❌ Don't Use Static Classes
```java
// BAD
@PreAuthorize("hasAuthority(T(com.stech.common.security.Permission).USER_READ)")
```

### ✅ Use String Directly
```java
// GOOD
@PreAuthorize("hasAuthority('USER_READ')")
```

### ❌ Don't Add ROLE_ Prefix Manually
```java
// BAD
@PreAuthorize("hasRole('ROLE_ADMIN')")
SecurityUtils.hasRole("ROLE_ADMIN")
```

### ✅ Spring Adds It Automatically
```java
// GOOD
@PreAuthorize("hasRole('ADMIN')")
SecurityUtils.hasRole("ADMIN")
```

---

## 📚 Documentation Files

1. **`FINAL_IMPLEMENTATION_SUMMARY.md`** - Complete implementation guide
2. **`DATABASE_SECURITY_GUIDE.md`** - Database setup and management
3. **`common-util/SECURITY_README.md`** - Security system overview
4. **`SECURITY_QUICK_REFERENCE.md`** - Detailed code examples
5. **`init-roles-permissions.sql`** - Database initialization

---

**Print this card and keep it handy!** 📌
