package com.cmm.mit.dto;

import java.math.BigDecimal;

public record ByCategoryItem(CategoryRef category, BigDecimal total, double percent) {}
