package com.example.subscriptionmanager.infrastructure.config;

import com.example.subscriptionmanager.application.usecase.*;
import com.example.subscriptionmanager.domain.exchange.ExchangeRateProvider;
import com.example.subscriptionmanager.domain.repository.SubscriptionRepository;
import com.example.subscriptionmanager.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Currency;

/**
 * Application configuration for use cases and services.
 */
@Configuration
public class ApplicationConfig {
    
    @Value("${app.base-currency:PLN}")
    private String baseCurrencyCode;

    @Bean
    public AddSubscriptionUseCase addSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        return new AddSubscriptionUseCase(subscriptionRepository);
    }

    @Bean
    public CancelSubscriptionUseCase cancelSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        return new CancelSubscriptionUseCase(subscriptionRepository);
    }

    @Bean
    public ListSubscriptionsUseCase listSubscriptionsUseCase(SubscriptionRepository subscriptionRepository) {
        return new ListSubscriptionsUseCase(subscriptionRepository);
    }

    @Bean
    public CalculateMonthlyCostUseCase calculateMonthlyCostUseCase(
            SubscriptionRepository subscriptionRepository,
            ExchangeRateProvider exchangeRateProvider) {
        Currency baseCurrency = Currency.getInstance(baseCurrencyCode);
        return new CalculateMonthlyCostUseCase(subscriptionRepository, exchangeRateProvider, baseCurrency);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new RegisterUserUseCase(userRepository, passwordEncoder, jwtService);
    }

    @Bean
    public LoginUseCase loginUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new LoginUseCase(userRepository, passwordEncoder, jwtService);
    }
}
