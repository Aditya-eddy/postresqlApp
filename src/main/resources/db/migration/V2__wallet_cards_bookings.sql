-- Wallet, travel cards, bookings, payments, reviews.

CREATE TABLE IF NOT EXISTS wallets (
    id BIGSERIAL PRIMARY KEY,
    member_id VARCHAR(64) NOT NULL UNIQUE,
    wallet_id VARCHAR(64) NOT NULL UNIQUE,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_wallets_member ON wallets (member_id);

CREATE TABLE IF NOT EXISTS travel_cards (
    id BIGSERIAL PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL,
    whitelabel VARCHAR(32) NOT NULL,
    card_type VARCHAR(16) NOT NULL,         -- VIRTUAL, PHYSICAL
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, FROZEN
    balance_minor BIGINT NOT NULL DEFAULT 0,
    masked_pan VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cards_account ON travel_cards (account_id);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_ref VARCHAR(32) NOT NULL UNIQUE,
    account_id VARCHAR(64) NOT NULL,
    whitelabel VARCHAR(32) NOT NULL,
    booking_type VARCHAR(16) NOT NULL,      -- HOTEL, FLIGHT
    status VARCHAR(16) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, CANCELLED
    total_amount_minor BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    -- hotel fields
    hotel_name VARCHAR(256),
    destination VARCHAR(256),
    check_in DATE,
    check_out DATE,
    rooms INT,
    guests INT,
    -- flight fields
    airline VARCHAR(128),
    flight_number VARCHAR(32),
    origin VARCHAR(8),
    flight_destination VARCHAR(8),
    departure_date DATE,
    return_date DATE,
    passengers INT,
    cabin_class VARCHAR(16),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_bookings_account ON bookings (account_id);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    payment_ref VARCHAR(32) NOT NULL UNIQUE,
    account_id VARCHAR(64) NOT NULL,
    whitelabel VARCHAR(32) NOT NULL,
    card_id BIGINT,
    booking_ref VARCHAR(32),
    amount_minor BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_method VARCHAR(16) NOT NULL,    -- TRAVEL_CARD, CREDIT_CARD
    status VARCHAR(16) NOT NULL DEFAULT 'COMPLETED', -- COMPLETED, REFUNDED
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_payments_account ON payments (account_id);
CREATE INDEX IF NOT EXISTS idx_payments_booking ON payments (booking_ref);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL,
    whitelabel VARCHAR(32) NOT NULL,
    booking_ref VARCHAR(32) NOT NULL,
    property_name VARCHAR(256) NOT NULL,
    rating INT NOT NULL,                    -- 1..10
    title VARCHAR(256) NOT NULL,
    comment TEXT NOT NULL,
    travel_type VARCHAR(16) NOT NULL,       -- SOLO, COUPLE, FAMILY, BUSINESS
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_reviews_account ON reviews (account_id);
CREATE INDEX IF NOT EXISTS idx_reviews_booking ON reviews (booking_ref);
