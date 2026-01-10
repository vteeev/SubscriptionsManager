CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_amount DECIMAL(19, 2) NOT NULL,
    price_currency VARCHAR(3) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    next_payment_date DATE NOT NULL,
    auto_renewal BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_user_id_status ON subscriptions(user_id, status);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
