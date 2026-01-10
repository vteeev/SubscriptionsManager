package com.example.subscriptionmanager.domain;

import com.example.subscriptionmanager.domain.exchange.ExchangeRateProvider;
import com.example.subscriptionmanager.domain.model.*;
import com.example.subscriptionmanager.domain.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillingServiceTest {

    private ExchangeRateProvider exchangeRateProvider;
    private BillingService billingService;
    private Currency baseCurrency;

    @BeforeEach
    void setUp() {
        exchangeRateProvider = mock(ExchangeRateProvider.class);
        baseCurrency = Currency.getInstance("PLN");
        billingService = new BillingService(exchangeRateProvider, baseCurrency);
    }

    @Test
    void shouldCalculateMonthlyCostForSingleMonthlySubscription() {
        Subscription subscription = createSubscription(
                "Spotify",
                new Money(BigDecimal.valueOf(29.99), Currency.getInstance("PLN")),
                BillingCycle.MONTHLY
        );

        when(exchangeRateProvider.convert(any(Money.class), eq(baseCurrency)))
                .thenAnswer(invocation -> {
                    Money money = invocation.getArgument(0);
                    return Optional.of(money);
                });

        Money result = billingService.calculateMonthlyCost(List.of(subscription));

        assertEquals(BigDecimal.valueOf(29.99), result.getAmount());
        assertEquals("PLN", result.getCurrencyCode());
    }

    @Test
    void shouldCalculateMonthlyCostForYearlySubscription() {
        Subscription subscription = createSubscription(
                "Netflix",
                new Money(BigDecimal.valueOf(600), Currency.getInstance("PLN")),
                BillingCycle.YEARLY
        );

        when(exchangeRateProvider.convert(any(Money.class), eq(baseCurrency)))
                .thenAnswer(invocation -> {
                    Money money = invocation.getArgument(0);
                    return Optional.of(money);
                });

        Money result = billingService.calculateMonthlyCost(List.of(subscription));

        assertEquals(0, BigDecimal.valueOf(50.00).compareTo(result.getAmount()));
        assertEquals("PLN", result.getCurrencyCode());
    }

    @Test
    void shouldExcludeTrialSubscriptions() {
        Subscription trial = createSubscription(
                "Trial",
                new Money(BigDecimal.valueOf(100), Currency.getInstance("PLN")),
                BillingCycle.TRIAL
        );

        when(exchangeRateProvider.convert(any(Money.class), eq(baseCurrency)))
                .thenAnswer(invocation -> {
                    Money money = invocation.getArgument(0);
                    return Optional.of(money);
                });

        Money result = billingService.calculateMonthlyCost(List.of(trial));

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getAmount()));
    }

    @Test
    void shouldExcludeCancelledSubscriptions() {
        Subscription cancelled = createSubscription(
                "Cancelled",
                new Money(BigDecimal.valueOf(29.99), Currency.getInstance("PLN")),
                BillingCycle.MONTHLY
        );
        cancelled.cancel();

        when(exchangeRateProvider.convert(any(Money.class), eq(baseCurrency)))
                .thenAnswer(invocation -> {
                    Money money = invocation.getArgument(0);
                    return Optional.of(money);
                });

        Money result = billingService.calculateMonthlyCost(List.of(cancelled));

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getAmount()));
    }

    @Test
    void shouldCalculateTotalForMultipleSubscriptions() {
        Subscription sub1 = createSubscription(
                "Spotify",
                new Money(BigDecimal.valueOf(29.99), Currency.getInstance("PLN")),
                BillingCycle.MONTHLY
        );
        Subscription sub2 = createSubscription(
                "Netflix",
                new Money(BigDecimal.valueOf(50), Currency.getInstance("PLN")),
                BillingCycle.MONTHLY
        );

        when(exchangeRateProvider.convert(any(Money.class), eq(baseCurrency)))
                .thenAnswer(invocation -> {
                    Money money = invocation.getArgument(0);
                    return Optional.of(money);
                });

        Money result = billingService.calculateMonthlyCost(List.of(sub1, sub2));

        assertEquals(BigDecimal.valueOf(79.99), result.getAmount());
    }

    @Test
    void shouldConvertCurrencyWhenCalculating() {
        Subscription subscription = createSubscription(
                "US Service",
                new Money(BigDecimal.valueOf(10), Currency.getInstance("USD")),
                BillingCycle.MONTHLY
        );

        Money convertedMoney = new Money(BigDecimal.valueOf(40), Currency.getInstance("PLN"));
        when(exchangeRateProvider.convert(any(Money.class), eq(baseCurrency)))
                .thenReturn(Optional.of(convertedMoney));

        Money result = billingService.calculateMonthlyCost(List.of(subscription));

        assertEquals(0, BigDecimal.valueOf(40).compareTo(result.getAmount()));
        assertEquals("PLN", result.getCurrencyCode());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyConversionFails() {
        Subscription subscription = createSubscription(
                "Foreign Service",
                new Money(BigDecimal.valueOf(10), Currency.getInstance("EUR")),
                BillingCycle.MONTHLY
        );

        when(exchangeRateProvider.convert(any(Money.class), eq(baseCurrency)))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            billingService.calculateMonthlyCost(List.of(subscription));
        });
    }

    private Subscription createSubscription(String name, Money price, BillingCycle billingCycle) {
        return Subscription.create(
                SubscriptionId.newId(),
                UserId.newId(),
                name,
                price,
                billingCycle,
                LocalDate.now().plusMonths(1),
                true
        );
    }
}
