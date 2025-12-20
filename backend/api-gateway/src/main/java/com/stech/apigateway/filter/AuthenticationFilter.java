package com.stech.apigateway.filter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stech.apigateway.exception.CustomJwtTokenException;
import com.stech.apigateway.exception.CustomUnauthorizedException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_SERVICE_URL = "http://AUTH-SERVICE/api/v1/auth/validate-token";
    private static final String IP_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final String API_PATH_PREFIX = "/api";
    private static final String IP_ADDRESS = "ipAddress";
    private final RouteValidator validator;
    private final WebClient.Builder webClientBuilder;
    private final Gson gson;

    public AuthenticationFilter(RouteValidator validator, WebClient.Builder webClientBuilder, Gson gson) {
        super(Config.class);
        this.validator = validator;
        this.webClientBuilder = webClientBuilder;
        this.gson = gson;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!validator.isSecured.test(exchange.getRequest())) {
                return chain.filter(exchange);
            }

            try {
                String authHeader = getAuthorizationHeader(exchange);
                String token = extractToken(authHeader);
                String extractedPath = extractApiPath(exchange);
                HttpMethod method = exchange.getRequest().getMethod();

                Map<String, String> requestMap = createValidationRequest(token, extractedPath, method, exchange);

                return validateToken(requestMap)
                        .flatMap(responseEntity -> processValidToken(responseEntity, exchange, authHeader))
                        .then(chain.filter(exchange))
                        .onErrorResume(WebClientResponseException.class, this::handleWebClientError);
            } catch (Exception e) {
                return Mono.error(e);
            }
        };
    }

    private String getAuthorizationHeader(ServerWebExchange exchange) {
        //log.info("Authorization header: {}", exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new CustomUnauthorizedException("Missing authorization header");
        }
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new CustomJwtTokenException("Invalid authorization header format");
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }

    private Map<String, String> createValidationRequest(String token, String apiPath, HttpMethod method, ServerWebExchange exchange) {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("token", token);
        requestMap.put("requiredPermissionsApi", apiPath);
        requestMap.put("requiredPermissionsMethod", method.name());
        requestMap.put(IP_ADDRESS, extractClientIp(exchange));
        return requestMap;
    }

    private Mono<ResponseEntity<String>> validateToken(Map<String, String> requestMap) {
        return webClientBuilder.build()
                .post()
                .uri(AUTH_SERVICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + requestMap.get("token"))
                .bodyValue(requestMap)
                .retrieve()
                .toEntity(String.class);
    }

    private Mono<Void> processValidToken(ResponseEntity<String> responseEntity, ServerWebExchange exchange, String authHeader) {
        HttpStatusCode status = responseEntity.getStatusCode();
        if (!status.is2xxSuccessful()) {
            String errorMsg = status.is4xxClientError() 
                ? "Invalid token! Status: " + status.value() 
                : "Server error during token validation. Status: " + status.value();
            return Mono.error(new CustomJwtTokenException(errorMsg));
        }

        String responseBody = responseEntity.getBody();
        // log.info("Response Body: {}", responseBody);
        
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        // log.info("Response Body Message processValidToken: {}", jsonResponse);
        JsonObject data = jsonResponse.get("data").getAsJsonObject();
        String ipAddress = data.get(IP_ADDRESS).getAsString();
        String userId = data.get("userId").getAsString();

        exchange.getRequest().mutate()
                .header(IP_ADDRESS, ipAddress)
                .header("userId", userId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();

        return Mono.empty();
    }

    private Mono<Void> handleWebClientError(WebClientResponseException ex) {
        String messageString = ex.getResponseBodyAsString();
        JsonObject jsonResponse = gson.fromJson(messageString, JsonObject.class);
        String message = jsonResponse.get("message").getAsString();
        return Mono.error(new CustomJwtTokenException(message));
    }

    public static String extractApiPath(ServerWebExchange exchange) {
        String requestUrl = exchange.getRequest().getURI().toString();
        int index = requestUrl.indexOf(API_PATH_PREFIX);
        return index >= 0 ? requestUrl.substring(index) : null;
    }

    private String extractClientIp(ServerWebExchange exchange) {
        InetSocketAddress xForwardedForHeader = exchange.getRequest().getRemoteAddress();
        if (xForwardedForHeader != null && xForwardedForHeader.getHostString() != null) {
            return extractValidIpFromHeader(xForwardedForHeader.getHostString());
        }

        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return remoteAddress.getAddress().getHostAddress();
        }

        return "-";
    }

    private String extractValidIpFromHeader(String ipHeader) {
        String[] ips = ipHeader.split(",");
        for (String ip : ips) {
            ip = ip.trim();
            if (!ip.isEmpty() && isValidIp(ip)) {
                return ip;
            }
        }
        return "-";
    }

    private boolean isValidIp(String ip) {
        return ip.matches(IP_REGEX);
    }

    public static class Config {
        Config() {
            log.info("AuthenticationFilter Config");
        }
    }
}