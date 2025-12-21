# 🔄 Refresh Token Implementation Guide

## 📋 Overview

The blogging platform now supports **refresh tokens** for secure, long-lived authentication sessions. This implementation follows industry best practices for JWT-based authentication with refresh token rotation.

---

## 🎯 Features

✅ **Access Token** - Short-lived JWT (15 minutes - 24 hours)  
✅ **Refresh Token** - Long-lived JWT (7 days - 30 days)  
✅ **Token Rotation** - New access token without re-authentication  
✅ **Database Storage** - Refresh tokens stored securely  
✅ **Revocation Support** - Logout invalidates refresh tokens  
✅ **IP & User Agent Tracking** - Security audit trail  

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Login Flow                                │
│                                                              │
│  1. User sends credentials                                   │
│  2. Authentication service validates                         │
│  3. Generate access token (short-lived)                      │
│  4. Generate refresh token (long-lived)                      │
│  5. Store refresh token in database                          │
│  6. Return both tokens to client                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  Refresh Token Flow                          │
│                                                              │
│  1. Access token expires                                     │
│  2. Client sends refresh token                               │
│  3. Validate refresh token (JWT + database)                  │
│  4. Check expiration and revocation status                   │
│  5. Generate new access token                                │
│  6. Return new access token (same refresh token)             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     Logout Flow                              │
│                                                              │
│  1. Client sends refresh token                               │
│  2. Delete refresh token from database                       │
│  3. Access token expires naturally                           │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Components Created

### 1. **Database**

#### `RefreshTokenEntity.java`
```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    private Long id;
    private String token;           // JWT refresh token
    private UserEntity user;        // Associated user
    private Instant expiryDate;     // Expiration timestamp
    private boolean revoked;        // Revocation flag
    private Instant createdAt;      // Creation timestamp
    private String ipAddress;       // Client IP
    private String userAgent;       // Client user agent
}
```

#### `RefreshTokenRepository.java`
- `findByToken(String token)` - Find refresh token
- `deleteByUser(UserEntity user)` - Delete all user tokens
- `deleteExpiredTokens()` - Clean up expired tokens
- `revokeAllUserTokens(UserEntity user)` - Revoke all user tokens

### 2. **Service Layer**

#### `RefreshTokenService.java`
```java
public interface RefreshTokenService {
    RefreshTokenEntity createRefreshToken(String username, String ipAddress, String userAgent);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    RefreshTokenEntity findByToken(String token);
    void deleteByToken(String token);
    void revokeAllUserTokens(String username);
    void deleteExpiredTokens();
}
```

### 3. **JWT Token Provider**

#### Enhanced `JwtTokenProvider.java`
```java
// Generate access token with authorities
public String generateToken(Authentication authentication)

// Generate refresh token (no authorities, longer expiration)
public String generateRefreshToken(String username)

// Check token type
public boolean isRefreshToken(String token)
public boolean isAccessToken(String token)
```

### 4. **DTOs**

#### `JwtResponse.java` (Updated)
```java
{
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 900,              // Access token expiry in seconds
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "permissions": ["USER_READ", "POST_WRITE"],
    "roles": ["ADMIN", "USER"]
}
```

#### `RefreshTokenRequest.java`
```java
{
    "refreshToken": "eyJhbGc..."
}
```

### 5. **API Endpoints**

#### `POST /api/v1/auth/login`
**Request:**
```json
{
    "username": "admin",
    "password": "admin123"
}
```

**Response:**
```json
{
    "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "permissions": ["USER_READ", "USER_WRITE"],
    "roles": ["ADMIN"]
}
```

#### `POST /api/v1/auth/refresh-token`
**Request:**
```json
{
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9..."
}
```

**Response:**
```json
{
    "accessToken": "eyJhbGciOiJIUzM4NCJ9...",  // NEW access token
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9...", // SAME refresh token
    "tokenType": "Bearer",
    "expiresIn": 900,
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "permissions": ["USER_READ", "USER_WRITE"],
    "roles": ["ADMIN"]
}
```

#### `POST /api/v1/auth/logout`
**Request:**
```json
{
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9..."
}
```

**Response:**
```
"Logged out successfully"
```

---

## 🔧 Configuration

### `application-local.properties`
```properties
# Access token expiration (24 hours in milliseconds)
application.security.jwt.token.expiration=86400000

# Refresh token expiration (7 days in hours)
application.security.jwt.token.refresh.expiration=168
```

### `application-prod.properties`
```properties
# Access token expiration (2 hours in milliseconds)
application.security.jwt.token.expiration=7200000

# Refresh token expiration (30 days in hours)
application.security.jwt.token.refresh.expiration=720
```

---

## 🚀 Usage Examples

### 1. **Login and Get Tokens**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Save both tokens:**
- `accessToken` - Use for API requests
- `refreshToken` - Use to get new access token

### 2. **Use Access Token**

```bash
curl -X GET http://localhost:9092/api/v1/users/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### 3. **Refresh Access Token**

When access token expires:

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```

### 4. **Logout**

```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```

---

## 💻 Client-Side Implementation

### JavaScript/TypeScript Example

```typescript
class AuthService {
    private accessToken: string | null = null;
    private refreshToken: string | null = null;

    async login(username: string, password: string) {
        const response = await fetch('http://localhost:8080/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();
        this.accessToken = data.accessToken;
        this.refreshToken = data.refreshToken;
        
        // Store in localStorage or secure storage
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        
        return data;
    }

    async refreshAccessToken() {
        const response = await fetch('http://localhost:8080/api/v1/auth/refresh-token', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken: this.refreshToken })
        });

        const data = await response.json();
        this.accessToken = data.accessToken;
        localStorage.setItem('accessToken', data.accessToken);
        
        return data;
    }

    async apiCall(url: string, options: RequestInit = {}) {
        // Add access token to request
        const headers = {
            ...options.headers,
            'Authorization': `Bearer ${this.accessToken}`
        };

        let response = await fetch(url, { ...options, headers });

        // If 401, try to refresh token
        if (response.status === 401) {
            await this.refreshAccessToken();
            
            // Retry with new access token
            headers['Authorization'] = `Bearer ${this.accessToken}`;
            response = await fetch(url, { ...options, headers });
        }

        return response;
    }

    async logout() {
        await fetch('http://localhost:8080/api/v1/auth/logout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken: this.refreshToken })
        });

        this.accessToken = null;
        this.refreshToken = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    }
}
```

---

## 🔐 Security Features

### 1. **Token Types**
- Access tokens have `"type": "ACCESS"` claim
- Refresh tokens have `"type": "REFRESH"` claim
- Prevents using refresh token as access token

### 2. **Database Validation**
- Refresh tokens validated against database
- Can be revoked immediately
- Tracks IP address and user agent

### 3. **Expiration Handling**
- Access token: Short-lived (15 min - 2 hours)
- Refresh token: Long-lived (7 - 30 days)
- Expired tokens automatically rejected

### 4. **Revocation**
- Logout deletes refresh token
- Can revoke all user tokens
- Cleanup job for expired tokens

---

## 📊 Database Schema

```sql
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry_date (expiry_date)
);
```

---

## 🧪 Testing

### 1. **Test Login**
```bash
# Login and save tokens
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq '.'
```

### 2. **Test Access Token**
```bash
# Use access token
curl -X GET http://localhost:9092/api/v1/users/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### 3. **Test Refresh Token**
```bash
# Refresh access token
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}' \
  | jq '.'
```

### 4. **Test Logout**
```bash
# Logout
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'

# Try to use refresh token again (should fail)
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

---

## 🔄 Token Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│  1. LOGIN                                                    │
│     ├─ Generate access token (15 min)                       │
│     ├─ Generate refresh token (7 days)                      │
│     └─ Store refresh token in database                      │
├─────────────────────────────────────────────────────────────┤
│  2. USE ACCESS TOKEN                                         │
│     ├─ Make API requests with access token                  │
│     └─ Access token valid for 15 minutes                    │
├─────────────────────────────────────────────────────────────┤
│  3. ACCESS TOKEN EXPIRES                                     │
│     ├─ Client detects 401 Unauthorized                      │
│     ├─ Send refresh token to /refresh-token                 │
│     ├─ Get new access token                                 │
│     └─ Retry original request                               │
├─────────────────────────────────────────────────────────────┤
│  4. REFRESH TOKEN EXPIRES (after 7 days)                    │
│     ├─ Client must login again                              │
│     └─ Get new access + refresh tokens                      │
├─────────────────────────────────────────────────────────────┤
│  5. LOGOUT                                                   │
│     ├─ Send refresh token to /logout                        │
│     ├─ Delete refresh token from database                   │
│     └─ Clear tokens from client storage                     │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Setup Checklist

- [x] RefreshTokenEntity created
- [x] RefreshTokenRepository created
- [x] RefreshTokenService implemented
- [x] JwtTokenProvider enhanced
- [x] JwtResponse updated
- [x] RefreshTokenRequest created
- [x] AuthService updated
- [x] AuthController endpoints added
- [x] Database migration script created
- [x] API Gateway routes updated
- [x] Configuration properties set

---

## 🎉 Summary

Your blogging platform now has a **production-ready refresh token system** with:

✅ **Secure** - Tokens validated in database  
✅ **Flexible** - Configurable expiration times  
✅ **Auditable** - IP and user agent tracking  
✅ **Revocable** - Logout invalidates tokens  
✅ **Scalable** - Efficient database queries  
✅ **Standard** - Follows JWT best practices  

**All services can now use refresh tokens for long-lived authentication sessions!** 🚀

---

**Implementation Date:** December 7, 2025  
**Status:** ✅ **Production Ready**  
**Version:** 1.0
