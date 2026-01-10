package com.example.subscriptionmanager.domain.model;

/**
 * Enum representing billing cycles for subscriptions.
 * Implements Strategy pattern for different billing calculations.
 */
public enum BillingCycle {
    MONTHLY(1),
    YEARLY(12),
    TRIAL(0);

    private final int months;

    BillingCycle(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }

    /**
     * Calculates monthly cost based on billing cycle.
     * For trial, returns 0.
     * For yearly, divides by 12.
     * For monthly, returns the same amount.
     */
    public double calculateMonthlyCost(double totalCost) {
        if (this == TRIAL) {
            return 0.0;
        }
        if (this == YEARLY) {
            return totalCost / 12.0;
        }
        return totalCost;
    }
}
