package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.AccountType;
import java.util.UUID;

public record AccountRef(UUID id, String name, AccountType type) {}
