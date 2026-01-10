package com.example.subscriptionmanager.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for SubscriptionEntity.
 */
@Repository
public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    List<SubscriptionEntity> findByUserId(UUID userId);
    
    List<SubscriptionEntity> findByUserIdAndStatus(UUID userId, SubscriptionEntity.SubscriptionStatusEnum status);
}
