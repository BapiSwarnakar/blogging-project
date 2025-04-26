package com.stech.apigateway.filter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import com.stech.apigateway.exception.CustomJwtTokenException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClient;
    @Autowired
    private Gson gson;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                // Check if the header contains the token
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new CustomJwtTokenException("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                    
                   
                    String extractedPath = extractApiPath(exchange);
                    if(extractedPath == null) {
                    	throw new CustomJwtTokenException("Invalid request URL (missing '/api')");
                    }
                    
                    HttpMethod method = exchange.getRequest().getMethod();
                    
                    Map<String,String> requestMap = new HashMap<>();
                   
                    requestMap.put("token", authHeader);
                    requestMap.put("requiredPermissionsApi", extractedPath);
                    requestMap.put("requiredPermissionsMethod", method.name());
                    requestMap.put("ipAddress", extractClientIp(exchange));
               
                    // Perform token validation asynchronously using WebClient
                    return webClient.build().post()
                            .uri("http://AUTH-SERVICE/api/v1/auth/validate-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(requestMap)
                            .retrieve()
                            .toEntity(String.class)
                            .flatMap(responseEntity -> {
                                HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                                if (status.is2xxSuccessful()) {
                                    // Token is valid, proceed with the request
                                    String responseBody = responseEntity.getBody();
                                    System.out.println("Response Body: " + responseBody);
                                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                                    String ipAddress = jsonResponse.get("ipAddress").getAsString();
                                    String userId = jsonResponse.get("userId").getAsString();

                                	exchange.getRequest().mutate()
                                	.header("ipAddress", ipAddress)
                                	.header("userId", userId)
                                	.build();
                                    
                                	return chain.filter(exchange);
                                	
                                } else if (status.is4xxClientError()) {
                                    // Handle 4xx client errors
                                    return Mono.error(new CustomJwtTokenException("Invalid token! Status: " + status.value()));
                                } else {
                                    // Handle 5xx server errors
                                    return Mono.error(new CustomJwtTokenException("Server error during token validation. Status: " + status.value()));
                                }
                            })
                            .onErrorResume(WebClientResponseException.class, ex -> {
                                String messageString = ex.getResponseBodyAsString();
                                
                                JsonObject jsonResponse = gson.fromJson(messageString, JsonObject.class);
                                String message = jsonResponse.get("message").getAsString();
                                
                                return Mono.error(new CustomJwtTokenException(message));
                            });
                } else {
                    throw new CustomJwtTokenException("Invalid authorization header format");
                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuration properties for the filter if needed
    }
    public static String extractApiPath(ServerWebExchange exchange) {
    	
        String requestUrl = exchange.getRequest().getURI().toString();
        
        int index = requestUrl.indexOf("/api");

        if (index >= 0) {
            return requestUrl.substring(index);
        } else {
            return  null;
        }
        
    }
        
    private String extractClientIp(ServerWebExchange exchange) {
        InetSocketAddress xForwardedForHeader = exchange.getRequest().getRemoteAddress();
        
        if (xForwardedForHeader != null && xForwardedForHeader.getHostString() != null) {
        	String ipAddress = xForwardedForHeader.getHostString();
            String[] ips = ipAddress.split(",");
            for (String ip : ips) {
                ip = ip.trim();
                if (!ip.isEmpty() && isValidIp(ip)) {
                    return ip;
                }
            }
        }
        
        // Fallback to remote address if X-Forwarded-For is not available or valid
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return remoteAddress.getAddress().getHostAddress();
        }

        // Fallback return if no IP is found
        return "-";
    }


	private boolean isValidIp(String ip) {
	    // Simple IP validation using regex
	    String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	    return ip.matches(ipRegex);
	}
}
