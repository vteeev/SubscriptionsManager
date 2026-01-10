package com.example.subscriptionmanager.application.dto;

/**
 * DTO for monthly cost calculation result.
 */
public record MonthlyCostDto(
        Double amount,
        String currency
) {
}
