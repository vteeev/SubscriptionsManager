package com.example.subscriptionmanager.domain.repository;

import com.example.subscriptionmanager.domain.model.User;
import com.example.subscriptionmanager.domain.model.UserId;

import java.util.Optional;

/**
 * Repository interface for User aggregate.
 */
public interface UserRepository {
    /**
     * Saves a user.
     */
    User save(User user);

    /**
     * Finds user by ID.
     */
    Optional<User> findById(UserId userId);

    /**
     * Finds user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if user exists by email.
     */
    boolean existsByEmail(String email);
}
