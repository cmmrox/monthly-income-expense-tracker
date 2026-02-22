package com.cmm.mit.dto;

import java.time.LocalDate;
import java.util.List;

public record ByCategoryResponse(LocalDate from, LocalDate to, List<ByCategoryItem> items) {}
