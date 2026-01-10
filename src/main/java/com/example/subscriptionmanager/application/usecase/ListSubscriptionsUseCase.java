package com.example.subscriptionmanager.application.usecase;

import com.example.subscriptionmanager.application.dto.SubscriptionDto;
import com.example.subscriptionmanager.domain.model.Subscription;
import com.example.subscriptionmanager.domain.model.UserId;
import com.example.subscriptionmanager.domain.repository.SubscriptionRepository;

import java.util.List;
import java.util.UUID;

/**
 * Use case for listing user subscriptions.
 */
public class ListSubscriptionsUseCase {
    private final SubscriptionRepository subscriptionRepository;

    public ListSubscriptionsUseCase(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<SubscriptionDto> execute(String userIdString) {
        UserId userId = new UserId(UUID.fromString(userIdString));
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        
        return subscriptions.stream()
                .map(this::toDto)
                .toList();
    }

    public List<SubscriptionDto> executeActive(String userIdString) {
        UserId userId = new UserId(UUID.fromString(userIdString));
        List<Subscription> subscriptions = subscriptionRepository.findActiveByUserId(userId);
        
        return subscriptions.stream()
                .map(this::toDto)
                .toList();
    }

    private SubscriptionDto toDto(Subscription subscription) {
        return new SubscriptionDto(
                subscription.getSubscriptionId().getValue().toString(),
                subscription.getUserId().getValue().toString(),
                subscription.getName(),
                subscription.getPrice().getAmount().doubleValue(),
                subscription.getPrice().getCurrencyCode(),
                subscription.getBillingCycle().name(),
                subscription.getNextPaymentDate(),
                subscription.isAutoRenewal(),
                subscription.getStatus().name()
        );
    }
}
