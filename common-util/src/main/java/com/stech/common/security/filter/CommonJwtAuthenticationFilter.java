package com.stech.common.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.stech.common.library.JwtTokenLibrary;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralized JWT Authentication Filter
 * All microservices should extend this class or use it directly
 * This ensures consistent JWT validation across all services
 */
@Slf4j
public class CommonJwtAuthenticationFilter extends OncePerRequestFilter {

    // Common public endpoints that should skip JWT processing
    private static final String[] DEFAULT_PUBLIC_URLS = {
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
    private String[] additionalPublicUrls = new String[0];

    /**
     * Constructor with no additional public URLs
     */
    public CommonJwtAuthenticationFilter() {
        this(new String[0]);
    }

    /**
     * Constructor with additional public URLs specific to the service
     * @param additionalPublicUrls Service-specific public URLs
     */
    public CommonJwtAuthenticationFilter(String[] additionalPublicUrls) {
        this.additionalPublicUrls = additionalPublicUrls;
    }

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
        
        log.info("Processing request: {} {}", method, requestURI);
        
        // Skip JWT processing for public URLs
        if (isPublicUrl(requestURI)) {
            log.info("Skipping JWT processing for public URL: {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No or invalid Authorization header found for URL: {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Authorization header found for URL starting with Bearer");
        final String jwt = authHeader.substring(7);

        try {
            if (JwtTokenLibrary.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = JwtTokenLibrary.getUsernameFromToken(jwt);
                List<String> roles = JwtTokenLibrary.getAuthorities(jwt);
                Collection<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Successfully authenticated user: {} with authorities: {}", username, roles);
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

        log.info("Authorization ended");
        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request URL matches any of the public URLs
     */
    private boolean isPublicUrl(String requestURI) {
        // Check default public URLs
        for (String publicUrl : DEFAULT_PUBLIC_URLS) {
            if (pathMatcher.match(publicUrl, requestURI)) {
                return true;
            }
        }
        
        // Check additional service-specific public URLs
        for (String publicUrl : additionalPublicUrls) {
            if (pathMatcher.match(publicUrl, requestURI)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get all public URLs (default + additional)
     */
    protected String[] getAllPublicUrls() {
        return Arrays.copyOf(
            Arrays.stream(DEFAULT_PUBLIC_URLS)
                .toList()
                .toArray(new String[0]), 
            DEFAULT_PUBLIC_URLS.length + additionalPublicUrls.length
        );
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
