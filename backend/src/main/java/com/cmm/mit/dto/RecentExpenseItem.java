package com.cmm.mit.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RecentExpenseItem(
    UUID id,
    Instant txnDate,
    BigDecimal amount,
    CategoryRef category,
    AccountRef account,
    String description,
    String merchant
) {}
