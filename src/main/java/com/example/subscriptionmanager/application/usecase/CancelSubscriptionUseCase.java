package com.example.subscriptionmanager.application.usecase;

import com.example.subscriptionmanager.domain.model.SubscriptionId;
import com.example.subscriptionmanager.domain.repository.SubscriptionRepository;

/**
 * Use case for cancelling a subscription.
 */
public class CancelSubscriptionUseCase {
    private final SubscriptionRepository subscriptionRepository;

    public CancelSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void execute(String subscriptionIdString) {
        SubscriptionId subscriptionId = new SubscriptionId(java.util.UUID.fromString(subscriptionIdString));
        
        var subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionIdString));

        subscription.cancel();
        subscriptionRepository.save(subscription);
    }
}
