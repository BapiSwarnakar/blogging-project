package com.stech.authentication.external.impl;

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

import com.stech.authentication.exception.CustomRuntimeException;
import com.stech.authentication.external.ExternalUserService;
import com.stech.common.library.CommunicationServiceUtil;
import com.stech.common.resilience.annotation.GlobalCircuitBreaker;
import com.stech.common.resilience.annotation.GlobalRateLimiter;
import com.stech.common.resilience.annotation.GlobalRetry;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExternalUserServiceImpl implements ExternalUserService {

    private final RestTemplate restTemplate;

    public ExternalUserServiceImpl(
        @Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @GlobalRetry
    @GlobalRateLimiter
    @GlobalCircuitBreaker
    public String getUserAll() {
        String url = CommunicationServiceUtil.getUserManagementMicroserviceUrl() + "/internal/all";
        log.info("Executing getUserAll from Authentication Service...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};

        log.info("url: {}", url);
        log.info("Fetching user accounting software details");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        log.info("Response: {}", response.getBody());
        return response.getBody();
    }

}
