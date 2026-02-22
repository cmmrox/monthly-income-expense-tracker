package com.cmm.mit.controller;

import com.cmm.mit.dto.ByCategoryResponse;
import com.cmm.mit.dto.DailyTrendResponse;
import com.cmm.mit.dto.RecentExpenseItem;
import com.cmm.mit.dto.SummaryResponse;
import com.cmm.mit.service.DashboardService;
import com.cmm.mit.service.PeriodService;
import java.time.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Dashboard/reporting HTTP API.
 *
 * <p>Thin controller: computes default date range (salary-cycle period) and delegates to services.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

  private final PeriodService periodService;
  private final DashboardService dashboardService;

  /**
   * Get the dashboard summary for a date range.
   *
   * <p>If no range is provided, defaults to the current salary-cycle period.
   */
  @GetMapping("/dashboard/summary")
  public ResponseEntity<SummaryResponse> summary(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    PeriodService.Period period = periodService.currentPeriod(Clock.systemUTC());
    LocalDate effectiveFrom = from != null ? from : period.start();
    LocalDate effectiveTo = to != null ? to : period.end();

    return ResponseEntity.ok(dashboardService.summary(effectiveFrom, effectiveTo));
  }

  /**
   * Get recent expense transactions.
   */
  @GetMapping("/dashboard/recent-expenses")
  public ResponseEntity<List<RecentExpenseItem>> recent(@RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(dashboardService.recentExpenses(limit));
  }

  /**
   * Report: expense totals grouped by category.
   */
  @GetMapping("/reports/expenses/by-category")
  public ResponseEntity<ByCategoryResponse> byCategory(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    return ResponseEntity.ok(dashboardService.expenseByCategory(from, to));
  }

  /**
   * Report: daily expense trend for a date range.
   */
  @GetMapping("/reports/expenses/daily-trend")
  public ResponseEntity<DailyTrendResponse> dailyTrend(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    return ResponseEntity.ok(dashboardService.dailyExpenseTrend(from, to));
  }
}
