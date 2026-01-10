package com.example.subscriptionmanager.infrastructure.persistence.jpa;

import com.example.subscriptionmanager.domain.model.*;
import com.example.subscriptionmanager.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of SubscriptionRepository.
 * Bridges domain and infrastructure layers.
 */
@Component
public class JpaSubscriptionRepository implements SubscriptionRepository {
    private final SpringDataSubscriptionRepository springDataRepository;

    public JpaSubscriptionRepository(SpringDataSubscriptionRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        SubscriptionEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Subscription> findById(SubscriptionId subscriptionId) {
        return springDataRepository.findById(subscriptionId.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Subscription> findByUserId(UserId userId) {
        return springDataRepository.findByUserId(userId.getValue())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subscription> findActiveByUserId(UserId userId) {
        return springDataRepository.findByUserIdAndStatus(
                        userId.getValue(),
                        SubscriptionEntity.SubscriptionStatusEnum.ACTIVE)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(SubscriptionId subscriptionId) {
        springDataRepository.deleteById(subscriptionId.getValue());
    }

    @Override
    public boolean existsById(SubscriptionId subscriptionId) {
        return springDataRepository.existsById(subscriptionId.getValue());
    }

    private SubscriptionEntity toEntity(Subscription subscription) {
        return new SubscriptionEntity(
                subscription.getSubscriptionId().getValue(),
                subscription.getUserId().getValue(),
                subscription.getName(),
                subscription.getPrice().getAmount(),
                subscription.getPrice().getCurrencyCode(),
                mapBillingCycle(subscription.getBillingCycle()),
                subscription.getNextPaymentDate(),
                subscription.isAutoRenewal(),
                mapStatus(subscription.getStatus())
        );
    }

    private Subscription toDomain(SubscriptionEntity entity) {
        SubscriptionId subscriptionId = new SubscriptionId(entity.getId());
        UserId userId = new UserId(entity.getUserId());
        Money price = new Money(entity.getPriceAmount(), Currency.getInstance(entity.getPriceCurrency()));
        BillingCycle billingCycle = mapBillingCycle(entity.getBillingCycle());
        SubscriptionStatus status = mapStatus(entity.getStatus());

        Subscription subscription = Subscription.create(
                subscriptionId,
                userId,
                entity.getName(),
                price,
                billingCycle,
                entity.getNextPaymentDate(),
                entity.getAutoRenewal()
        );

        // If cancelled, we need to set status manually since create() sets it to ACTIVE
        if (status == SubscriptionStatus.CANCELLED) {
            subscription.cancel();
        }

        return subscription;
    }

    private SubscriptionEntity.BillingCycleEnum mapBillingCycle(BillingCycle billingCycle) {
        return switch (billingCycle) {
            case MONTHLY -> SubscriptionEntity.BillingCycleEnum.MONTHLY;
            case YEARLY -> SubscriptionEntity.BillingCycleEnum.YEARLY;
            case TRIAL -> SubscriptionEntity.BillingCycleEnum.TRIAL;
        };
    }

    private BillingCycle mapBillingCycle(SubscriptionEntity.BillingCycleEnum billingCycle) {
        return switch (billingCycle) {
            case MONTHLY -> BillingCycle.MONTHLY;
            case YEARLY -> BillingCycle.YEARLY;
            case TRIAL -> BillingCycle.TRIAL;
        };
    }

    private SubscriptionEntity.SubscriptionStatusEnum mapStatus(SubscriptionStatus status) {
        return switch (status) {
            case ACTIVE -> SubscriptionEntity.SubscriptionStatusEnum.ACTIVE;
            case CANCELLED -> SubscriptionEntity.SubscriptionStatusEnum.CANCELLED;
        };
    }

    private SubscriptionStatus mapStatus(SubscriptionEntity.SubscriptionStatusEnum status) {
        return switch (status) {
            case ACTIVE -> SubscriptionStatus.ACTIVE;
            case CANCELLED -> SubscriptionStatus.CANCELLED;
        };
    }
}
