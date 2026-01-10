package com.example.subscriptionmanager.application.usecase;

import com.example.subscriptionmanager.application.dto.CreateSubscriptionCommand;
import com.example.subscriptionmanager.application.dto.SubscriptionDto;
import com.example.subscriptionmanager.domain.model.*;
import com.example.subscriptionmanager.domain.repository.SubscriptionRepository;

/**
 * Use case for adding a new subscription.
 */
public class AddSubscriptionUseCase {
    private final SubscriptionRepository subscriptionRepository;

    public AddSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public SubscriptionDto execute(CreateSubscriptionCommand command) {
        // Validate and convert input
        SubscriptionId subscriptionId = SubscriptionId.newId();
        UserId userId = new UserId(java.util.UUID.fromString(command.userId()));
        Money price = new Money(command.price(), command.currency());
        BillingCycle billingCycle = BillingCycle.valueOf(command.billingCycle().toUpperCase());
        
        // Create subscription
        Subscription subscription = Subscription.create(
                subscriptionId,
                userId,
                command.name(),
                price,
                billingCycle,
                command.nextPaymentDate(),
                command.autoRenewal()
        );

        // Save
        Subscription saved = subscriptionRepository.save(subscription);

        // Convert to DTO
        return toDto(saved);
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
