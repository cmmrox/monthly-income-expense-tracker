-- Performance/NFR: indexes aligned to common query patterns for large transaction volumes.

-- 1) Frequent pattern: filter by type + date range (and often order by txn_date)
CREATE INDEX IF NOT EXISTS idx_transactions_type_txn_date ON transactions(type, txn_date);

-- 2) Expense by category within a date window (type=EXPENSE is common)
CREATE INDEX IF NOT EXISTS idx_transactions_type_category_txn_date ON transactions(type, category_id, txn_date);

-- 3) Account-centric searches within date windows (account/from/to roles)
CREATE INDEX IF NOT EXISTS idx_transactions_account_txn_date ON transactions(account_id, txn_date);
CREATE INDEX IF NOT EXISTS idx_transactions_from_account_txn_date ON transactions(from_account_id, txn_date);
CREATE INDEX IF NOT EXISTS idx_transactions_to_account_txn_date ON transactions(to_account_id, txn_date);
