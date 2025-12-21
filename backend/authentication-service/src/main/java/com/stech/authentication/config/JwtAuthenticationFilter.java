package com.stech.authentication.config;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.stech.common.library.JwtTokenLibrary;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Authentication Filter for authentication-service
 * This service has special requirements as it handles user authentication
 * and needs to allow public access to login, register, refresh-token, and logout endpoints
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Define public endpoints that should skip JWT processing
    private static final String[] PUBLIC_URLS = {
        // Authentication endpoints (public access)
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/api/v1/auth/refresh-token",        
        // Swagger UI v3 (OpenAPI)
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/swagger-ui/index.html",
        "/swagger-ui/index.html/**",
        "/webjars/**",
        "/swagger-resources/**",
        "/swagger-resources",
        "/configuration/ui",
        "/configuration/security",
        
        // Actuator endpoints
        "/actuator/health",
        "/actuator/info",
        
        // API Documentation
        "/api-docs/**",
        "/api-docs.yaml",
        
        // Other public resources
        "/favicon.ico",
        "/error"
    };
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.debug("Checking if request should be filtered: {}", path);
        return isPublicUrl(path);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();
        
        log.debug("Processing request: {} {}", method, requestURI);
        
        // Skip JWT processing for public URLs
        if (isPublicUrl(requestURI)) {
            log.debug("Skipping JWT processing for public URL: {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // Check if Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No or invalid Authorization header found for URL: {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract JWT token
        final String jwt = authHeader.substring(7);

        try {
            if (JwtTokenLibrary.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = JwtTokenLibrary.getUsernameFromToken(jwt);
                List<String> authorities = JwtTokenLibrary.getAuthorities(jwt);
                Collection<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            grantedAuthorities
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JwtAuthenticationFilter: Authenticated user: {}, Authorities: {}", username, authorities);
            } else if (!JwtTokenLibrary.validateToken(jwt)) {
                log.error("Invalid or expired JWT token for URL: {} {}", method, requestURI);
                sendErrorResponse(response, 401, "JWT Token Error", "Invalid or expired JWT token. Please login again.");
                return;
            }
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            sendErrorResponse(response, 401, "JWT Token Error", "JWT token validation failed: " + e.getMessage());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Check if the request URL matches any of the public URLs
     */
    private boolean isPublicUrl(String requestURI) {
        for (String publicUrl : PUBLIC_URLS) {
            if (pathMatcher.match(publicUrl, requestURI)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Send error response as JSON
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}", 
            status, error, message
        ));
    }
}
