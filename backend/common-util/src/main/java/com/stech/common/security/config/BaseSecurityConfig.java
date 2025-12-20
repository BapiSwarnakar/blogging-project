package com.stech.common.security.config;

import java.util.Arrays;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.stech.common.security.filter.CommonJwtAuthenticationFilter;

/**
 * Base Security Configuration
 * Provides common security setup that can be extended by individual services
 */
public abstract class BaseSecurityConfig {

    /**
     * Get the JWT authentication filter for this service
     * Each service must provide its own filter instance
     */
    protected abstract CommonJwtAuthenticationFilter getJwtAuthenticationFilter();

    /**
     * Get service-specific public paths
     * Override this to add service-specific public endpoints
     */
    protected String[] getServiceSpecificPublicPaths() {
        return new String[0];
    }

    /**
     * Get common public paths that all services should allow
     */
    protected String[] getCommonPublicPaths() {
        return new String[]{
            // Swagger UI v3 (OpenAPI)
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
            "/favicon.ico",
            "/error",
            // Actuator endpoints
            "/actuator/health",
            "/actuator/info",
            // API Documentation
            "/api-docs/**",
            "/api-docs.yaml"
        };
    }

    /**
     * Get all public paths (common + service-specific)
     */
    protected String[] getAllPublicPaths() {
        String[] commonPaths = getCommonPublicPaths();
        String[] servicePaths = getServiceSpecificPublicPaths();
        
        String[] allPaths = new String[commonPaths.length + servicePaths.length];
        System.arraycopy(commonPaths, 0, allPaths, 0, commonPaths.length);
        System.arraycopy(servicePaths, 0, allPaths, commonPaths.length, servicePaths.length);
        
        return allPaths;
    }

    /**
     * Configure the security filter chain
     * Services can override this for custom configuration
     */
    public SecurityFilterChain configureSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(getAllPublicPaths()).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(getJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration
     * Override this method to customize CORS settings
     */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
