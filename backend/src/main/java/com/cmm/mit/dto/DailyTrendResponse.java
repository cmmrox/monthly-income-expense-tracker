package com.cmm.mit.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyTrendResponse(LocalDate from, LocalDate to, List<DailyPoint> points) {}
