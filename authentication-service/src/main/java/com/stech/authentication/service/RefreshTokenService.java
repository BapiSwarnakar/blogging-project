package com.stech.authentication.service;

import com.stech.authentication.entity.RefreshTokenEntity;

public interface RefreshTokenService {
    
    /**
     * Create and save a new refresh token for a user
     */
    RefreshTokenEntity createRefreshToken(String username, String ipAddress, String userAgent);
    
    /**
     * Verify if refresh token is valid and not expired
     */
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    
    /**
     * Find refresh token by token string
     */
    RefreshTokenEntity findByToken(String token);
    
    /**
     * Delete refresh token
     */
    void deleteByToken(String token);
    
    /**
     * Revoke all refresh tokens for a user
     */
    void revokeAllUserTokens(String username);
    
    /**
     * Clean up expired tokens
     */
    void deleteExpiredTokens();
}
