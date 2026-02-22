package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.CategoryType;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String name,
    CategoryType type,
    String color,
    String icon,
    boolean active,
    Instant createdAt,
    Instant updatedAt) {}
