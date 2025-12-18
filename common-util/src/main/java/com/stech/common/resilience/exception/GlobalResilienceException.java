package com.stech.common.resilience.exception;

import lombok.Getter;

@Getter
public class GlobalResilienceException extends RuntimeException {
    
    private final String serviceName;
    private final String type; // CIRCUIT_BREAKER, RATE_LIMITER, RETRY

    public GlobalResilienceException(String serviceName, String type, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.type = type;
    }
}
