package com.example.subscriptionmanager.application.dto;

import java.time.LocalDate;

/**
 * DTO for subscription representation.
 */
public record SubscriptionDto(
        String subscriptionId,
        String userId,
        String name,
        Double price,
        String currency,
        String billingCycle,
        LocalDate nextPaymentDate,
        Boolean autoRenewal,
        String status
) {
}
