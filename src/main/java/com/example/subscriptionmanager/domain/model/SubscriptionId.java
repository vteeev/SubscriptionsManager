package com.example.subscriptionmanager.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class SubscriptionId {
    private final UUID value;

    public SubscriptionId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static SubscriptionId newId() {
        return new SubscriptionId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionId that = (SubscriptionId) o;
        return Objects.equals(value, that.value);
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
