package com.stech.authentication.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stech.authentication.entity.RefreshTokenEntity;
import com.stech.authentication.entity.UserEntity;
import com.stech.authentication.exception.CustomResourceNotFoundException;
import com.stech.authentication.helper.JwtTokenProvider;
import com.stech.authentication.repository.RefreshTokenRepository;
import com.stech.authentication.repository.UserRepository;
import com.stech.authentication.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public RefreshTokenEntity createRefreshToken(String username, String ipAddress, String userAgent) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomResourceNotFoundException("User not found with username: " + username));

        // Generate refresh token JWT
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(username);

        // Calculate expiry date
        long refreshExpirationMs = jwtTokenProvider.getRefreshTokenExpirationInMilliseconds();
        Instant expiryDate = Instant.now().plusMillis(refreshExpirationMs);

        // Create and save refresh token entity
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(expiryDate)
                .createdAt(Instant.now())
                .revoked(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Created refresh token for user: {}", username);
        
        return refreshToken;
    }

    @Override
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }

        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked. Please login again.");
        }

        return token;
    }

    @Override
    public RefreshTokenEntity findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomResourceNotFoundException("Refresh token not found"));
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        RefreshTokenEntity refreshToken = findByToken(token);
        refreshTokenRepository.delete(refreshToken);
        log.info("Deleted refresh token for user: {}", refreshToken.getUser().getUsername());
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomResourceNotFoundException("User not found with username: " + username));
        
        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("Revoked all refresh tokens for user: {}", username);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens();
        log.info("Deleted expired refresh tokens");
    }
}
