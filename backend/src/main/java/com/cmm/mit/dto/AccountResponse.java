package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.AccountType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
    UUID id,
    String name,
    AccountType type,
    String currency,
    BigDecimal openingBalance,
    boolean active,
    Instant createdAt,
    Instant updatedAt) {}
