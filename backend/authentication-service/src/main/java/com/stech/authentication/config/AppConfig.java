package com.stech.authentication.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean(name = "loadBalancedRestTemplate")
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        return restTemplate;
    }

    @Primary
    @Bean(name = "externalRestTemplate")
    public RestTemplate externalRestTemplate() {
        return new RestTemplate();
    }

}
