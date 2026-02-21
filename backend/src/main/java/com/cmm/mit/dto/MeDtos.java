package com.cmm.mit.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public class MeDtos {
  public record MeResponse(
      UUID id,
      String baseCurrency,
      int periodStartDay,
      Instant createdAt,
      Instant updatedAt) {}

  public record PatchSettingsRequest(
      @NotBlank @Size(min = 3, max = 3) String baseCurrency,
      @Min(1) @Max(28) int periodStartDay
  ) {}
}
