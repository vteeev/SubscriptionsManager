package com.example.subscriptionmanager.application.dto;

/**
 * Command DTO for user registration.
 */
public record RegisterUserCommand(
        String email,
        String password
) {
}
