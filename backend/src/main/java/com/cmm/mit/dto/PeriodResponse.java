package com.cmm.mit.dto;

import java.time.LocalDate;

public record PeriodResponse(LocalDate periodStart, LocalDate periodEnd, int periodStartDay) {}
