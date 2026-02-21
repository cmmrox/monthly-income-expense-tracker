-- Note: gen_random_uuid() is provided by pgcrypto extension in Postgres.
-- For tests on H2, we avoid extension usage by supplying UUIDs in JPA (Hibernate) generation.

CREATE TABLE user_settings (
  id UUID PRIMARY KEY,
  base_currency VARCHAR(3) NOT NULL,
  period_start_day INT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(20) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  opening_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(10) NOT NULL,
  color VARCHAR(20),
  icon VARCHAR(50),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  txn_date TIMESTAMP WITH TIME ZONE NOT NULL,
  type VARCHAR(10) NOT NULL,
  amount NUMERIC(19,2) NOT NULL,

  account_id UUID NULL REFERENCES accounts(id),
  category_id UUID NULL REFERENCES categories(id),

  from_account_id UUID NULL REFERENCES accounts(id),
  to_account_id UUID NULL REFERENCES accounts(id),

  description VARCHAR(255),
  merchant VARCHAR(120),
  payment_method VARCHAR(40),

  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_txn_date ON transactions(txn_date);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_from_account_id ON transactions(from_account_id);
CREATE INDEX idx_transactions_to_account_id ON transactions(to_account_id);
