package com.stech.apigateway.filter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
                        .onErrorResume(WebClientResponseException.class, this::handleWebClientError)
                        .onErrorResume(e -> {
                            if (e instanceof CustomJwtTokenException || e instanceof CustomUnauthorizedException) {
                                return Mono.error(e);
                            }
                            log.error("Authentication filter unexpected error: {}", e.getMessage());
                            return Mono.error(new CustomJwtTokenException("Authentication service is currently unavailable or unreachable"));
                        });
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
        try {
            if (responseBody == null || responseBody.isBlank()) {
                return Mono.error(new CustomJwtTokenException("Authentication service returned an empty response"));
            }

            JsonElement element = gson.fromJson(responseBody, JsonElement.class);
            if (!element.isJsonObject()) {
                return Mono.error(new CustomJwtTokenException("Invalid response format from authentication service"));
            }

            JsonObject jsonResponse = element.getAsJsonObject();
            if (!jsonResponse.has("data") || jsonResponse.get("data").isJsonNull()) {
                return Mono.error(new CustomJwtTokenException("Authentication response missing user data"));
            }

            JsonObject data = jsonResponse.get("data").getAsJsonObject();
            String ipAddress = data.has(IP_ADDRESS) ? data.get(IP_ADDRESS).getAsString() : "-";
            String userId = data.has("userId") ? data.get("userId").getAsString() : "unknown";

            exchange.getRequest().mutate()
                    .header(IP_ADDRESS, ipAddress)
                    .header("userId", userId)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .build();

            return Mono.empty();
        } catch (Exception e) {
            log.error("Failed to parse authentication success response: {}. Error: {}", responseBody, e.getMessage());
            return Mono.error(new CustomJwtTokenException("Error processing authentication response"));
        }
    }

    private Mono<Void> handleWebClientError(WebClientResponseException ex) {
        log.error("Authentication service error: Status {}, Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        String messageString = ex.getResponseBodyAsString();
        String message = "Authentication service error (" + ex.getStatusCode().value() + ")";
        
        try {
            if (messageString != null && !messageString.isBlank()) {
                JsonElement element = gson.fromJson(messageString, JsonElement.class);
                if (element.isJsonObject()) {
                    JsonObject jsonResponse = element.getAsJsonObject();
                    if (jsonResponse.has("message")) {
                        message = jsonResponse.get("message").getAsString();
                    } else if (jsonResponse.has("error")) {
                        message = jsonResponse.get("error").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse authentication error response as JSON: {}", messageString);
        }
        
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