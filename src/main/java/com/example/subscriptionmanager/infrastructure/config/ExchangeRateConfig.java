package com.example.subscriptionmanager.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for exchange rate provider.
 */
@Configuration
public class ExchangeRateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
