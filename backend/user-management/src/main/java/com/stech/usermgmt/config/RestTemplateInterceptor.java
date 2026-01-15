package com.stech.usermgmt.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest originalRequest = attributes.getRequest();
            String token = originalRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null && !token.isEmpty()) {
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
            }

            String requestId = originalRequest.getHeader("X-Request-ID");
            if (requestId != null && !requestId.isEmpty()) {
                request.getHeaders().add("X-Request-ID", requestId);
            }
        }
        
        return execution.execute(request, body);
    }
}
