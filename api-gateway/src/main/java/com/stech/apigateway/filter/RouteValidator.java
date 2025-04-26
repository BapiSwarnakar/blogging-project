package com.stech.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class RouteValidator {

    public static final List<Pattern> openApiEndpoints = List.of(
            Pattern.compile("^/api/v1/auth/login$"),
            Pattern.compile("^/api/v1/user-mgmt/register$"),
            Pattern.compile("^/api/v1/auth/validate-user$"),
            Pattern.compile("^/api/v1/auth/forget-password$"),
            Pattern.compile("^/api/v1/auth/confirm-forget-password$"),
            Pattern.compile("^/api/v1/auth/refresh-token$"),
            Pattern.compile("^/eureka$"),
            Pattern.compile("^/api/v1/int/bdc/validate-phone-number$")    
        );

    public Predicate<ServerHttpRequest> isSecured =
        request -> openApiEndpoints
                .stream()
                //.peek(System.out::println)
                .noneMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());

}
