package com.example.subscriptionmanager.application.dto;

/**
 * Response DTO for authentication.
 */
public record AuthResponse(
        String token,
        String userId,
        String email
) {
}
