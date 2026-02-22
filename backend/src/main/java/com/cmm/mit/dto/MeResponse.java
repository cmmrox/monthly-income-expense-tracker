package com.cmm.mit.dto;

import java.time.Instant;
import java.util.UUID;

public record MeResponse(
    UUID id,
    String baseCurrency,
    int periodStartDay,
    Instant createdAt,
    Instant updatedAt
) {}
