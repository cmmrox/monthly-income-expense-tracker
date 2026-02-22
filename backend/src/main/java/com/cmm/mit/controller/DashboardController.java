package com.cmm.mit.controller;

import com.cmm.mit.dto.DashboardDtos;
import com.cmm.mit.service.DashboardService;
import com.cmm.mit.service.PeriodService;
import java.time.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

  private final PeriodService periodService;
  private final DashboardService dashboardService;

  @GetMapping("/dashboard/summary")
  public ResponseEntity<DashboardDtos.SummaryResponse> summary(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    PeriodService.Period period = periodService.currentPeriod(Clock.systemUTC());
    LocalDate effectiveFrom = from != null ? from : period.start();
    LocalDate effectiveTo = to != null ? to : period.end();

    return ResponseEntity.ok(dashboardService.summary(effectiveFrom, effectiveTo));
  }

  @GetMapping("/dashboard/recent-expenses")
  public ResponseEntity<List<DashboardDtos.RecentExpenseItem>> recent(@RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(dashboardService.recentExpenses(limit));
  }

  @GetMapping("/reports/expenses/by-category")
  public ResponseEntity<DashboardDtos.ByCategoryResponse> byCategory(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    return ResponseEntity.ok(dashboardService.expenseByCategory(from, to));
  }

  @GetMapping("/reports/expenses/daily-trend")
  public ResponseEntity<DashboardDtos.DailyTrendResponse> dailyTrend(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    return ResponseEntity.ok(dashboardService.dailyExpenseTrend(from, to));
  }
}
