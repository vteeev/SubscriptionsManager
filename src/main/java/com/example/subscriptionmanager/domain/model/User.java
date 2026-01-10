package com.example.subscriptionmanager.domain.model;

import java.util.Objects;

/**
 * Entity representing a user.
 */
public class User {
    private final UserId userId;
    private String email;
    private String passwordHash;
    private boolean active;

    private User(UserId userId) {
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.active = true;
    }

    /**
     * Factory method to create a new user.
     */
    public static User create(UserId userId, String email, String passwordHash) {
        User user = new User(userId);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        return user;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    private void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.toLowerCase().trim();
    }

    private void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
