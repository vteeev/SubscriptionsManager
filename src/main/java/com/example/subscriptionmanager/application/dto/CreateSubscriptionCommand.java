package com.example.subscriptionmanager.application.dto;

import java.time.LocalDate;

/**
 * Command DTO for creating a subscription.
 */
public record CreateSubscriptionCommand(
        String userId,
        String name,
        Double price,
        String currency,
        String billingCycle,
        LocalDate nextPaymentDate,
        Boolean autoRenewal
) {
}
