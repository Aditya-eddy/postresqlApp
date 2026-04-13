CREATE TABLE IF NOT EXISTS card_account_transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL,
    card_id VARCHAR(64),
    whitelabel VARCHAR(32) NOT NULL,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    amount_minor BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    merchant_name VARCHAR(128),
    wise_reference VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_tx_account ON card_account_transactions (account_id);
CREATE INDEX IF NOT EXISTS idx_tx_created ON card_account_transactions (created_at DESC);
