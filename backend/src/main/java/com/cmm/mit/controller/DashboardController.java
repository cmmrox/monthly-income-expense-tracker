package com.cmm.mit.controller;

import com.cmm.mit.dto.ApiEnvelope;
import com.cmm.mit.dto.DashboardDtos;
import com.cmm.mit.service.DashboardService;
import com.cmm.mit.service.PeriodService;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

  private final PeriodService periodService;
  private final DashboardService dashboardService;

  @GetMapping("/dashboard/summary")
  public ApiEnvelope<DashboardDtos.SummaryResponse> summary(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    PeriodService.Period p = periodService.currentPeriod(Clock.systemUTC());
    LocalDate f = from != null ? from : p.start();
    LocalDate t = to != null ? to : p.end();

    Instant fromI = f.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toEx = t.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    var s = dashboardService.summary(fromI, toEx);

    var rollups = new ArrayList<DashboardDtos.AccountRollup>();
    for (var a : s.accounts()) {
      var totals = s.byAccount().getOrDefault(a.getId(), com.cmm.mit.service.DashboardService.AccountTotals.empty());
      rollups.add(new DashboardDtos.AccountRollup(
          AccountController.toRef(a),
          totals.income(),
          totals.expense(),
          totals.transfersOut(),
          totals.transfersIn()
      ));
    }

    var resp = new DashboardDtos.SummaryResponse(
        f,
        t,
        s.incomeTotal(),
        s.expenseTotal(),
        s.incomeTotal().subtract(s.expenseTotal()),
        rollups
    );
    return ApiEnvelope.ok(resp);
  }

  @GetMapping("/dashboard/recent-expenses")
  public ApiEnvelope<List<DashboardDtos.RecentExpenseItem>> recent(@RequestParam(defaultValue = "10") int limit) {
    var items = dashboardService.recentExpenses(Math.min(limit, 50)).stream()
        .map(t -> new DashboardDtos.RecentExpenseItem(
            t.getId(),
            t.getTxnDate(),
            t.getAmount(),
            CategoryController.toRef(t.getCategory()),
            AccountController.toRef(t.getAccount()),
            t.getDescription(),
            t.getMerchant()))
        .toList();
    return ApiEnvelope.ok(items);
  }

  @GetMapping("/reports/expenses/by-category")
  public ApiEnvelope<DashboardDtos.ByCategoryResponse> byCategory(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    Instant fromI = from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toEx = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    var sums = dashboardService.expenseByCategory(fromI, toEx);
    BigDecimal grand = sums.stream().map(DashboardService.CategorySum::total).reduce(BigDecimal.ZERO, BigDecimal::add);

    var items = sums.stream().map(s -> {
      double pct = grand.compareTo(BigDecimal.ZERO) == 0 ? 0.0 : s.total().multiply(BigDecimal.valueOf(100)).divide(grand, 2, java.math.RoundingMode.HALF_UP).doubleValue();
      return new DashboardDtos.ByCategoryItem(CategoryController.toRef(s.category()), s.total(), pct);
    }).toList();

    return ApiEnvelope.ok(new DashboardDtos.ByCategoryResponse(from, to, items));
  }

  @GetMapping("/reports/expenses/daily-trend")
  public ApiEnvelope<DashboardDtos.DailyTrendResponse> dailyTrend(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    Instant fromI = from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toEx = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    var points = dashboardService.dailyExpenseTrend(fromI, toEx).stream()
        .map(p -> new DashboardDtos.DailyPoint(p.date(), p.total()))
        .toList();

    return ApiEnvelope.ok(new DashboardDtos.DailyTrendResponse(from, to, points));
  }
}
