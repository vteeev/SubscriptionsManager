package com.example.subscriptionmanager.application.usecase;

import com.example.subscriptionmanager.application.dto.MonthlyCostDto;
import com.example.subscriptionmanager.domain.exchange.ExchangeRateProvider;
import com.example.subscriptionmanager.domain.model.Money;
import com.example.subscriptionmanager.domain.model.Subscription;
import com.example.subscriptionmanager.domain.model.UserId;
import com.example.subscriptionmanager.domain.repository.SubscriptionRepository;
import com.example.subscriptionmanager.domain.service.BillingService;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Use case for calculating monthly cost of all subscriptions.
 */
public class CalculateMonthlyCostUseCase {
    private final SubscriptionRepository subscriptionRepository;
    private final BillingService billingService;

    public CalculateMonthlyCostUseCase(
            SubscriptionRepository subscriptionRepository,
            ExchangeRateProvider exchangeRateProvider,
            Currency baseCurrency) {
        this.subscriptionRepository = subscriptionRepository;
        this.billingService = new BillingService(exchangeRateProvider, baseCurrency);
    }

    public MonthlyCostDto execute(String userIdString) {
        UserId userId = new UserId(UUID.fromString(userIdString));
        List<Subscription> subscriptions = subscriptionRepository.findActiveByUserId(userId);
        
        Money monthlyCost = billingService.calculateMonthlyCost(subscriptions);
        
        return new MonthlyCostDto(
                monthlyCost.getAmount().doubleValue(),
                monthlyCost.getCurrencyCode()
        );
    }
}
