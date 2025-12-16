package com.stech.common.resilience.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RateLimiter(name = "globalRateLimiter")
public @interface GlobalRateLimiter {
}

