package com.stech.usermgmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.stech.common.security.filter.CommonJwtAuthenticationFilter;
import com.stech.common.security.util.SecurityUtils;

import java.util.Arrays;

/**
 * Security configuration for user-management service
 * Uses centralized CommonJwtAuthenticationFilter from common-util
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    /**
     * Create CommonJwtAuthenticationFilter bean
     * No service-specific public URLs for user-management
     */
    @Bean
    CommonJwtAuthenticationFilter jwtAuthenticationFilter() {
        // No additional public URLs for this service
        return new CommonJwtAuthenticationFilter(new String[0]);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(SecurityUtils.DEFAULT_PUBLIC_URLS).permitAll()
                .requestMatchers("/api/v1/users/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(SecurityUtils.USER_ALLOWED_ORIGIN_URLS);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("x-auth-token", "authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
