package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull CategoryType type,
    @Size(max = 20) String color,
    @Size(max = 50) String icon,
    @NotNull Boolean active
) {}
