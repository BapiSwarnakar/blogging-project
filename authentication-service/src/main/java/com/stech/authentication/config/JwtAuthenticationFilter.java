package com.stech.authentication.config;

import java.io.IOException;
import java.util.Arrays;
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
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
