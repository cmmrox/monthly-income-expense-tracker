package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateTxnRequest(
    @NotNull Instant txnDate,
    @NotNull TransactionType type,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
    UUID accountId,
    UUID categoryId,
    @Size(max = 255) String description,
    @Size(max = 120) String merchant,
    @Size(max = 40) String paymentMethod
) {}
