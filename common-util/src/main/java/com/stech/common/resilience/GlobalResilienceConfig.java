package com.stech.common.resilience;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

@Configuration
public class GlobalResilienceConfig {

    /* ---------------- RETRY ---------------- */

    @Bean
    public RetryConfig defaultRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(Exception.class)
                .build();
    }

    @Bean
    public RetryRegistry retryRegistry(RetryConfig defaultRetryConfig) {
        RetryRegistry registry = RetryRegistry.of(defaultRetryConfig);
        registry.retry("globalRetry", defaultRetryConfig);
        return registry;
    }

    /* ---------------- RATE LIMITER ---------------- */

    @Bean
    public RateLimiterConfig defaultRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(2))
                .build();
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry(
            RateLimiterConfig defaultRateLimiterConfig) {
        RateLimiterRegistry registry = RateLimiterRegistry.of(defaultRateLimiterConfig);
        registry.rateLimiter("globalRateLimiter", defaultRateLimiterConfig);
        return registry;
    }

    /* ---------------- CIRCUIT BREAKER ---------------- */

    @Bean
    public CircuitBreakerConfig defaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(
            CircuitBreakerConfig defaultCircuitBreakerConfig) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(defaultCircuitBreakerConfig);
        registry.circuitBreaker("globalCircuitBreaker", defaultCircuitBreakerConfig);
        return registry;
    }
}
