package com.example.subscriptionmanager.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class UserId {

    private final UUID value;

    public UserId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}