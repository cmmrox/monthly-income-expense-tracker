package com.cmm.mit.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyPoint(LocalDate date, BigDecimal total) {}
