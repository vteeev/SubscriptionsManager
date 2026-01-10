package com.example.subscriptionmanager.domain.repository;

import com.example.subscriptionmanager.domain.model.Subscription;
import com.example.subscriptionmanager.domain.model.SubscriptionId;
import com.example.subscriptionmanager.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Subscription aggregate.
 * Domain layer defines the contract, infrastructure implements it.
 */
public interface SubscriptionRepository {
    /**
     * Saves a subscription.
     */
    Subscription save(Subscription subscription);

    /**
     * Finds subscription by ID.
     */
    Optional<Subscription> findById(SubscriptionId subscriptionId);

    /**
     * Finds all subscriptions for a user.
     */
    List<Subscription> findByUserId(UserId userId);

    /**
     * Finds all active subscriptions for a user.
     */
    List<Subscription> findActiveByUserId(UserId userId);

    /**
     * Deletes a subscription.
     */
    void delete(SubscriptionId subscriptionId);

    /**
     * Checks if subscription exists.
     */
    boolean existsById(SubscriptionId subscriptionId);
}
