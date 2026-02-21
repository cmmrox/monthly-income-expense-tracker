package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.AccountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountDtos {
  public record AccountResponse(
      UUID id,
      String name,
      AccountType type,
      String currency,
      BigDecimal openingBalance,
      boolean active,
      Instant createdAt,
      Instant updatedAt) {}

  public record AccountRef(UUID id, String name, AccountType type) {}

  public record CreateAccountRequest(
      @NotBlank @Size(max = 100) String name,
      @NotNull AccountType type,
      @NotBlank @Size(min = 3, max = 3) String currency,
      @NotNull BigDecimal openingBalance
  ) {}

  public record UpdateAccountRequest(
      @NotBlank @Size(max = 100) String name,
      @NotNull AccountType type,
      @NotBlank @Size(min = 3, max = 3) String currency,
      @NotNull BigDecimal openingBalance,
      @NotNull Boolean active
  ) {}
}
