package com.stech.usermgmt.external.impl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stech.usermgmt.external.ExternalAuthenticationService;
import com.stech.common.library.CommunicationServiceUtil;
import com.stech.common.resilience.annotation.GlobalCircuitBreaker;
import com.stech.common.resilience.annotation.GlobalRateLimiter;
import com.stech.common.resilience.annotation.GlobalRetry;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExternalAuthenticationServiceImpl implements ExternalAuthenticationService {

    private final RestTemplate restTemplate;

    public ExternalAuthenticationServiceImpl(
        @Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @GlobalRetry(serviceName = "Authentication Service")
    @GlobalRateLimiter(serviceName = "Authentication Service")
    @GlobalCircuitBreaker(serviceName = "Authentication Service")
    public String getUserById(Long userId) {
        String url = CommunicationServiceUtil.getAuthenticationServiceMicroserviceUrl() + "/internal/user/" + userId;
        log.info("Executing getUserById from Authentication Service...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};

        log.info("url: {}", url);
        log.info("Fetching user details");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        log.info("Response: {}", response.getBody());
        return response.getBody();
    }

}
