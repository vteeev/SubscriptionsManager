package com.example.subscriptionmanager.domain;

import com.example.subscriptionmanager.domain.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {

    @Test
    void shouldCreateActiveSubscription() {
        SubscriptionId subscriptionId = SubscriptionId.newId();
        UserId userId = UserId.newId();
        Money price = new Money(BigDecimal.valueOf(29.99), Currency.getInstance("PLN"));
        
        Subscription subscription = Subscription.create(
                subscriptionId,
                userId,
                "Spotify",
                price,
                BillingCycle.MONTHLY,
                LocalDate.now().plusMonths(1),
                true
        );
        
        assertEquals(subscriptionId, subscription.getSubscriptionId());
        assertEquals(userId, subscription.getUserId());
        assertEquals("Spotify", subscription.getName());
        assertEquals(price, subscription.getPrice());
        assertEquals(BillingCycle.MONTHLY, subscription.getBillingCycle());
        assertTrue(subscription.isAutoRenewal());
        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertTrue(subscription.isActive());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        SubscriptionId subscriptionId = SubscriptionId.newId();
        UserId userId = UserId.newId();
        Money price = new Money(BigDecimal.valueOf(29.99), Currency.getInstance("PLN"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            Subscription.create(
                    subscriptionId,
                    userId,
                    "",
                    price,
                    BillingCycle.MONTHLY,
                    LocalDate.now().plusMonths(1),
                    true
            );
        });
    }

    @Test
    void shouldThrowExceptionWhenNextPaymentDateIsInPast() {
        SubscriptionId subscriptionId = SubscriptionId.newId();
        UserId userId = UserId.newId();
        Money price = new Money(BigDecimal.valueOf(29.99), Currency.getInstance("PLN"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            Subscription.create(
                    subscriptionId,
                    userId,
                    "Spotify",
                    price,
                    BillingCycle.MONTHLY,
                    LocalDate.now().minusDays(1),
                    true
            );
        });
    }

    @Test
    void shouldCancelSubscription() {
        Subscription subscription = createTestSubscription();
        
        subscription.cancel();
        
        assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
        assertFalse(subscription.isActive());
    }

    @Test
    void shouldThrowExceptionWhenCancellingAlreadyCancelledSubscription() {
        Subscription subscription = createTestSubscription();
        subscription.cancel();
        
        assertThrows(IllegalStateException.class, subscription::cancel);
    }

    @Test
    void shouldUpdateSubscription() {
        Subscription subscription = createTestSubscription();
        Money newPrice = new Money(BigDecimal.valueOf(39.99), Currency.getInstance("PLN"));
        
        subscription.update(
                "Netflix",
                newPrice,
                BillingCycle.YEARLY,
                LocalDate.now().plusYears(1),
                false
        );
        
        assertEquals("Netflix", subscription.getName());
        assertEquals(newPrice, subscription.getPrice());
        assertEquals(BillingCycle.YEARLY, subscription.getBillingCycle());
        assertFalse(subscription.isAutoRenewal());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCancelledSubscription() {
        Subscription subscription = createTestSubscription();
        subscription.cancel();
        Money newPrice = new Money(BigDecimal.valueOf(39.99), Currency.getInstance("PLN"));
        
        assertThrows(IllegalStateException.class, () -> {
            subscription.update(
                    "Netflix",
                    newPrice,
                    BillingCycle.YEARLY,
                    LocalDate.now().plusYears(1),
                    false
            );
        });
    }

    private Subscription createTestSubscription() {
        SubscriptionId subscriptionId = SubscriptionId.newId();
        UserId userId = UserId.newId();
        Money price = new Money(BigDecimal.valueOf(29.99), Currency.getInstance("PLN"));
        
        return Subscription.create(
                subscriptionId,
                userId,
                "Spotify",
                price,
                BillingCycle.MONTHLY,
                LocalDate.now().plusMonths(1),
                true
        );
    }
}
