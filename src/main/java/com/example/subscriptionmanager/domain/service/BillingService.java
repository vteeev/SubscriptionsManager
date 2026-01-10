package com.example.subscriptionmanager.domain.service;

import com.example.subscriptionmanager.domain.exchange.ExchangeRateProvider;
import com.example.subscriptionmanager.domain.model.Money;
import com.example.subscriptionmanager.domain.model.Subscription;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.List;

/**
 * Domain service for billing calculations.
 * Handles complex business logic involving multiple aggregates.
 */
public class BillingService {
    private final ExchangeRateProvider exchangeRateProvider;
    private final Currency baseCurrency;

    public BillingService(ExchangeRateProvider exchangeRateProvider, Currency baseCurrency) {
        this.exchangeRateProvider = exchangeRateProvider;
        this.baseCurrency = baseCurrency;
    }

    /**
     * Calculates total monthly cost for a list of subscriptions.
     * Converts all subscriptions to base currency and sums them up.
     */
    public Money calculateMonthlyCost(List<Subscription> subscriptions) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Subscription subscription : subscriptions) {
            if (!subscription.isActive()) {
                continue;
            }

            Money subscriptionPrice = subscription.getPrice();
            double monthlyCost = subscription.getBillingCycle()
                    .calculateMonthlyCost(subscriptionPrice.getAmount().doubleValue());

            Money monthlyMoney = new Money(BigDecimal.valueOf(monthlyCost), subscriptionPrice.getCurrency());

            // Convert to base currency
            Money converted = exchangeRateProvider.convert(monthlyMoney, baseCurrency)
                    .orElseThrow(() -> new IllegalStateException(
                            "Cannot convert " + monthlyMoney.getCurrencyCode() + " to " + baseCurrency.getCurrencyCode()));

            totalAmount = totalAmount.add(converted.getAmount());
        }

        return new Money(totalAmount.setScale(2, RoundingMode.HALF_UP), baseCurrency);
    }
}
