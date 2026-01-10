package com.example.subscriptionmanager.infrastructure.persistence.jpa;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity for Subscription.
 * Maps domain model to database schema.
 */
@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycleEnum billingCycle;

    @Column(name = "next_payment_date", nullable = false)
    private LocalDate nextPaymentDate;

    @Column(name = "auto_renewal", nullable = false)
    private Boolean autoRenewal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatusEnum status;

    // JPA requires no-arg constructor
    protected SubscriptionEntity() {
    }

    public SubscriptionEntity(
            UUID id,
            UUID userId,
            String name,
            BigDecimal priceAmount,
            String priceCurrency,
            BillingCycleEnum billingCycle,
            LocalDate nextPaymentDate,
            Boolean autoRenewal,
            SubscriptionStatusEnum status) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.billingCycle = billingCycle;
        this.nextPaymentDate = nextPaymentDate;
        this.autoRenewal = autoRenewal;
        this.status = status;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public BillingCycleEnum getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycleEnum billingCycle) {
        this.billingCycle = billingCycle;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public SubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatusEnum status) {
        this.status = status;
    }

    // Enums for JPA
    public enum BillingCycleEnum {
        MONTHLY, YEARLY, TRIAL
    }

    public enum SubscriptionStatusEnum {
        ACTIVE, CANCELLED
    }
}
