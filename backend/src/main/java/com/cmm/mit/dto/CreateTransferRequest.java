package com.cmm.mit.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateTransferRequest(
    @NotNull Instant txnDate,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
    @NotNull UUID fromAccountId,
    @NotNull UUID toAccountId,
    @Size(max = 255) String description
) {}
