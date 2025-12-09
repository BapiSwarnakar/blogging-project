package com.stech.authentication.helper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.io.Decoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.stech.authentication.service.impl.CustomUserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${application.security.jwt.token.secret-key}")
    private String jwtSecret;

    @Value("${application.security.jwt.token.expiration}")
    private long jwtExpirationMs;

    @Value("${application.security.jwt.token.refresh.expiration:168}") // Default 7 days in hours
    private long refreshTokenExpirationHours;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate access token with authorities
     */
    public String generateToken(Authentication authentication, String ipAddress, String userAgent) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        log.info("Generating access token for user: {}", authentication.getName());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String permissions = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setId(userDetails.getId().toString())
                .setSubject(userDetails.getEmail())
                .claim("type", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("ipAddress", ipAddress)
                .claim("userAgent", userAgent)
                .claim("permissions", permissions)
                .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    /**
     * Generate refresh token (longer expiration, no authorities)
     */
    public String generateRefreshToken(Long userId, String username) {
        Date now = new Date();
        long refreshExpirationMs = refreshTokenExpirationHours * 60 * 60 * 1000; // Convert hours to ms
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .setId(userId.toString())
                .setSubject(username)
                .claim("type", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return "REFRESH".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token is an access token
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return "ACCESS".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationInMilliseconds() {
        return jwtExpirationMs;
    }

    public long getRefreshTokenExpirationInMilliseconds() {
        return refreshTokenExpirationHours * 60 * 60 * 1000;
    }
}
