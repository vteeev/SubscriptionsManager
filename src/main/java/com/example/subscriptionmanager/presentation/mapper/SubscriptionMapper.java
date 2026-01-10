package com.example.subscriptionmanager.presentation.mapper;

import com.example.subscriptionmanager.application.dto.CreateSubscriptionCommand;
import com.example.subscriptionmanager.application.dto.MonthlyCostDto;
import com.example.subscriptionmanager.application.dto.SubscriptionDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting between presentation and application layers.
 */
@Component
public class SubscriptionMapper {
    
    public CreateSubscriptionCommand toCommand(CreateSubscriptionRequest request, String userId) {
        return new CreateSubscriptionCommand(
                userId,
                request.name(),
                request.price(),
                request.currency(),
                request.billingCycle(),
                request.nextPaymentDate(),
                request.autoRenewal()
        );
    }

    public SubscriptionResponse toResponse(SubscriptionDto dto) {
        return new SubscriptionResponse(
                dto.subscriptionId(),
                dto.userId(),
                dto.name(),
                dto.price(),
                dto.currency(),
                dto.billingCycle(),
                dto.nextPaymentDate(),
                dto.autoRenewal(),
                dto.status()
        );
    }

    public List<SubscriptionResponse> toResponseList(List<SubscriptionDto> dtos) {
        return dtos.stream()
                .map(this::toResponse)
                .toList();
    }

    public MonthlyCostResponse toResponse(MonthlyCostDto dto) {
        return new MonthlyCostResponse(dto.amount(), dto.currency());
    }

    // Request/Response DTOs for REST API
    public record CreateSubscriptionRequest(
            String name,
            Double price,
            String currency,
            String billingCycle,
            java.time.LocalDate nextPaymentDate,
            Boolean autoRenewal
    ) {
    }

    public record SubscriptionResponse(
            String subscriptionId,
            String userId,
            String name,
            Double price,
            String currency,
            String billingCycle,
            java.time.LocalDate nextPaymentDate,
            Boolean autoRenewal,
            String status
    ) {
    }

    public record MonthlyCostResponse(
            Double amount,
            String currency
    ) {
    }
}
