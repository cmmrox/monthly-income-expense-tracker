package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.CategoryType;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public class CategoryDtos {
  public record CategoryResponse(
      UUID id,
      String name,
      CategoryType type,
      String color,
      String icon,
      boolean active,
      Instant createdAt,
      Instant updatedAt) {}

  public record CategoryRef(UUID id, String name, CategoryType type, String color) {}

  public record CreateCategoryRequest(
      @NotBlank @Size(max = 100) String name,
      @NotNull CategoryType type,
      @Size(max = 20) String color,
      @Size(max = 50) String icon
  ) {}

  public record UpdateCategoryRequest(
      @NotBlank @Size(max = 100) String name,
      @NotNull CategoryType type,
      @Size(max = 20) String color,
      @Size(max = 50) String icon,
      @NotNull Boolean active
  ) {}
}
