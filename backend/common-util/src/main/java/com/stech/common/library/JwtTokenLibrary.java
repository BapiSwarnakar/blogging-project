package com.stech.common.library;

import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenLibrary {

    JwtTokenLibrary() {
        // Private constructor to prevent instantiation
        log.info("JwtTokenLibrary instantiated");
    }
    
    private static final String JWT_SECRET = "9C3953C4622DAD2D8A625DA26AA97508A994D75EA0EEB0EE75AFDA7A91317FA0";
    private static final long JWT_EXPIRATION_MS = 86400000; // 1 hour

    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String id = claims.getId();
        return id != null ? Long.parseLong(id) : null;
    }

    public static boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static long getExpirationInMilliseconds() {
        return JWT_EXPIRATION_MS;
    }

    public static List<String> getAuthorities(String token) {
        String auth = getAllClaimsFromToken(token).get("permissions", String.class);

        if (auth == null || auth.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(auth.split(","))
                .map(String::trim)
                .toList();
    }
}
