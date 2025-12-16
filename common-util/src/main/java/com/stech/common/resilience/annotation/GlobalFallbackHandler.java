package com.stech.common.resilience.annotation;

public class GlobalFallbackHandler {

    private static final String FALLBACK_MESSAGE = "Service temporarily unavailable. Please try again later.";

    private GlobalFallbackHandler() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Object fallback() {
        return FALLBACK_MESSAGE;
    }
}
