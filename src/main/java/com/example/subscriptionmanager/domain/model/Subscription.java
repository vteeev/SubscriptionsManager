package com.example.subscriptionmanager.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing a subscription.
 * Contains business logic for subscription management.
 */
public class Subscription {
    private final SubscriptionId subscriptionId;
    private final UserId userId;
    private String name;
    private Money price;
    private BillingCycle billingCycle;
    private LocalDate nextPaymentDate;
    private boolean autoRenewal;
    private SubscriptionStatus status;

    // Private constructor for factory methods
    private Subscription(SubscriptionId subscriptionId, UserId userId) {
        this.subscriptionId = Objects.requireNonNull(subscriptionId, "SubscriptionId cannot be null");
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.status = SubscriptionStatus.ACTIVE;
    }

    /**
     * Factory method to create a new subscription.
     */
    public static Subscription create(
            SubscriptionId subscriptionId,
            UserId userId,
            String name,
            Money price,
            BillingCycle billingCycle,
            LocalDate nextPaymentDate,
            boolean autoRenewal) {
        
        Subscription subscription = new Subscription(subscriptionId, userId);
        subscription.setName(name);
        subscription.setPrice(price);
        subscription.setBillingCycle(billingCycle);
        subscription.setNextPaymentDate(nextPaymentDate);
        subscription.setAutoRenewal(autoRenewal);
        
        return subscription;
    }

    /**
     * Updates subscription details.
     */
    public void update(String name, Money price, BillingCycle billingCycle, 
                      LocalDate nextPaymentDate, boolean autoRenewal) {
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update cancelled subscription");
        }
        
        this.setName(name);
        this.setPrice(price);
        this.setBillingCycle(billingCycle);
        this.setNextPaymentDate(nextPaymentDate);
        this.setAutoRenewal(autoRenewal);
    }

    /**
     * Cancels the subscription.
     */
    public void cancel() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Subscription is already cancelled");
        }
        this.status = SubscriptionStatus.CANCELLED;
    }

    /**
     * Checks if subscription is active.
     */
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    // Getters
    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public boolean isAutoRenewal() {
        return autoRenewal;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    // Setters with validation
    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subscription name cannot be null or empty");
        }
        this.name = name.trim();
    }

    private void setPrice(Money price) {
        this.price = Objects.requireNonNull(price, "Price cannot be null");
    }

    private void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = Objects.requireNonNull(billingCycle, "Billing cycle cannot be null");
    }

    private void setNextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = Objects.requireNonNull(nextPaymentDate, "Next payment date cannot be null");
        if (nextPaymentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Next payment date cannot be in the past");
        }
    }

    private void setAutoRenewal(boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(subscriptionId, that.subscriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriptionId);
    }
}
