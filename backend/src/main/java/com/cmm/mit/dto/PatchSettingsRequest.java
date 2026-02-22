package com.cmm.mit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatchSettingsRequest(
    @NotBlank @Size(min = 3, max = 3) String baseCurrency,
    @Min(1) @Max(28) int periodStartDay
) {}
