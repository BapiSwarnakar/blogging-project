package com.stech.authentication.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.stech.authentication.helper.JwtTokenProvider;
import com.stech.authentication.service.impl.CustomUserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtService;
    private final CustomUserDetailsServiceImpl customUserDetailsServiceImpl;

    // Define public endpoints that should skip JWT processing
    private static final String[] PUBLIC_URLS = {
        // Authentication endpoints
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        
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

    public JwtAuthenticationFilter(JwtTokenProvider jwtService, CustomUserDetailsServiceImpl customUserDetailsServiceImpl) {
        this.jwtService = jwtService;
        this.customUserDetailsServiceImpl = customUserDetailsServiceImpl;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        log.debug("Checking if request should be filtered: {}", path);
        return Arrays.stream(PUBLIC_URLS).anyMatch(pattern -> matcher.match(pattern, path));
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
        
        final String authHeader = request.getHeader("Authorization");
        
        // Check if Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No or invalid Authorization header found for URL: {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract JWT token
        final String jwt = authHeader.substring(7);
        
        try {
            // Extract username from JWT token
            String username = jwtService.getUsernameFromToken(jwt);
            
            if (username == null) {
                log.warn("Could not extract username from JWT token");
                filterChain.doFilter(request, response);
                return;
            }
            
            // If there's no existing authentication
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details
                UserDetails userDetails = this.customUserDetailsServiceImpl.loadUserByUsername(username);
                
                // Validate token
                if (jwtService.validateToken(jwt)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated user: {} for URL: {}", username, requestURI);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token for URL: {} - {}", requestURI, e.getMessage(), e);
            // Continue with the filter chain to let the security configuration handle the unauthorized request
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
}
