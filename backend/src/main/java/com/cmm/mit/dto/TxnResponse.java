package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TxnResponse(
    UUID id,
    Instant txnDate,
    TransactionType type,
    BigDecimal amount,
    AccountRef account,
    CategoryRef category,
    AccountRef fromAccount,
    AccountRef toAccount,
    String description,
    String merchant,
    String paymentMethod,
    Instant createdAt,
    Instant updatedAt
) {}
