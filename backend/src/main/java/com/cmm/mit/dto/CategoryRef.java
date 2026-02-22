package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.CategoryType;
import java.util.UUID;

public record CategoryRef(UUID id, String name, CategoryType type, String color) {}
