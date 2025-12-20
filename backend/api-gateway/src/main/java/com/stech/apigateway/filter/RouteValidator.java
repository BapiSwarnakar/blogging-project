package com.stech.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RouteValidator {
   
    RouteValidator() {
        log.info("RouteValidator constructor");
    }

    public final List<Pattern> openApiEndpoints = List.of(
            Pattern.compile("^/api/v1/auth/login$"),
            Pattern.compile("^/api/v1/auth/register$"),
            Pattern.compile("^/api/v1/auth/validate-token$"),
            Pattern.compile("^/api/v1/auth/refresh-token$"),
            Pattern.compile("^/api/v1/auth/logout$"),
            Pattern.compile("^/api/v1/auth/forget-password$"),
            Pattern.compile("^/api/v1/auth/confirm-forget-password$"),
            Pattern.compile("^/eureka$"),
            Pattern.compile("^/api/v1/int/bdc/validate-phone-number$")    
        );

    public final Predicate<ServerHttpRequest> isSecured =
        request -> openApiEndpoints
                .stream()
                //.peek(System.out::println)
                .noneMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());

}
