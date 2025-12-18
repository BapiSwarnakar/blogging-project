package com.stech.common.resilience.exception;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stech.common.library.GlobalApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalResilienceExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> handleCircuitBreakerOpenException(CallNotPermittedException ex) {
        log.warn("Circuit Breaker is open. Fallback triggered: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(GlobalApiResponse.error("Service temporarily unavailable. Please try again later.", ex.getMessage()));
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> handleRateLimiterException(RequestNotPermitted ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(GlobalApiResponse.error("Too many requests. Please try again later.", ex.getMessage()));
    }

    @ExceptionHandler(GlobalResilienceException.class)
    public ResponseEntity<GlobalApiResponse.ApiResult<Object>> handleGlobalResilienceException(GlobalResilienceException ex) {
        log.warn("Global Resilience exception triggered for service {}: {}", ex.getServiceName(), ex.getMessage());
        HttpStatus status = "RATE_LIMITER".equals(ex.getType()) ? HttpStatus.TOO_MANY_REQUESTS : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status)
                .body(GlobalApiResponse.error(ex.getMessage(), ex.getServiceName() + " Failure"));
    }
    
    // Add other handlers for Retry or Bulkhead if needed
}
