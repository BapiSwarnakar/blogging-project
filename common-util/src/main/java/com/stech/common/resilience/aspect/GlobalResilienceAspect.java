package com.stech.common.resilience.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.stech.common.resilience.annotation.GlobalCircuitBreaker;
import com.stech.common.resilience.annotation.GlobalRateLimiter;
import com.stech.common.resilience.annotation.GlobalRetry;
import com.stech.common.resilience.exception.GlobalResilienceException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@RequiredArgsConstructor
@Slf4j
public class GlobalResilienceAspect {

    private static final String FALLBACK_MESSAGE = " Service temporarily unavailable. Please try again later.";
    private static final String RATE_LIMIT_MESSAGE = " is receiving too many requests. Please try again later.";

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final RetryRegistry retryRegistry;

    @Around("@annotation(globalCircuitBreaker)")
    public Object handleCircuitBreaker(ProceedingJoinPoint joinPoint, GlobalCircuitBreaker globalCircuitBreaker) throws Throwable {
        String name = getInstanceName(joinPoint, globalCircuitBreaker.instanceName());
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(name);
        String serviceName = globalCircuitBreaker.serviceName();
        
        try {
            return cb.executeCheckedSupplier(joinPoint::proceed);
        } catch (CallNotPermittedException e) {
            log.warn("Circuit Breaker '{}' is OPEN for service: {}", name, serviceName);
            throw new GlobalResilienceException(serviceName, "CIRCUIT_BREAKER", serviceName + FALLBACK_MESSAGE, e);
        } catch (Throwable t) {
            log.error("Error calling service '{}' via Circuit Breaker '{}': {}", serviceName, name, t.getMessage());
            // We wrap the raw exception so the controller gets the service context
            throw new GlobalResilienceException(serviceName, "SERVICE_FAILURE", serviceName + FALLBACK_MESSAGE, t);
        }
    }

    @Around("@annotation(globalRateLimiter)")
    public Object handleRateLimiter(ProceedingJoinPoint joinPoint, GlobalRateLimiter globalRateLimiter) throws Throwable {
        String name = getInstanceName(joinPoint, globalRateLimiter.instanceName());
        RateLimiter rl = rateLimiterRegistry.rateLimiter(name);
        String serviceName = globalRateLimiter.serviceName();
        
        try {
            return rl.executeCheckedSupplier(joinPoint::proceed);
        } catch (RequestNotPermitted e) {
            log.warn("Rate Limit exceeded for service '{}' (Limiter: {})", serviceName, name);
            throw new GlobalResilienceException(serviceName, "RATE_LIMITER", serviceName + RATE_LIMIT_MESSAGE, e);
        } catch (Throwable t) {
            throw t;
        }
    }

    @Around("@annotation(globalRetry)")
    public Object handleRetry(ProceedingJoinPoint joinPoint, GlobalRetry globalRetry) throws Throwable {
        String name = getInstanceName(joinPoint, globalRetry.instanceName());
        Retry r = retryRegistry.retry(name);
        String serviceName = globalRetry.serviceName();
        
        try {
            return r.executeCheckedSupplier(joinPoint::proceed);
        } catch (Throwable t) {
            log.error("Retry failed for service '{}' after all attempts. Final error: {}", serviceName, t.getMessage());
            // Only wrap in ResilienceException if it's not already wrapped by CircuitBreaker aspect
            if (!(t instanceof GlobalResilienceException)) {
                throw new GlobalResilienceException(serviceName, "RETRY_FAILURE", serviceName + FALLBACK_MESSAGE, t);
            }
            throw t;
        }
    }

    private String getInstanceName(ProceedingJoinPoint joinPoint, String providedName) {
        if (providedName != null && !providedName.isEmpty()) {
            return providedName;
        }
        return joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
    }
}
