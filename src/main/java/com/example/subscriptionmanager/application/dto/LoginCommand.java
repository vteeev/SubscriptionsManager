package com.example.subscriptionmanager.application.dto;

/**
 * Command DTO for user login.
 */
public record LoginCommand(
        String email,
        String password
) {
}
