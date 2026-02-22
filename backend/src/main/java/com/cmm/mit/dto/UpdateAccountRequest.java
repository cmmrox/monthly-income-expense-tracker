package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateAccountRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull AccountType type,
    @NotBlank @Size(min = 3, max = 3) String currency,
    @NotNull BigDecimal openingBalance,
    @NotNull Boolean active
) {}
