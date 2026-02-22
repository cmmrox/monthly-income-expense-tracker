package com.cmm.mit.service;

import com.cmm.mit.dto.ByCategoryResponse;
import com.cmm.mit.dto.DailyTrendResponse;
import com.cmm.mit.dto.RecentExpenseItem;
import com.cmm.mit.dto.SummaryResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard and reporting service API.
 *
 * <p>Provides aggregated views for the UI: summaries, recent expenses and report rollups.
 */
public interface DashboardService {

  /**
   * Build the dashboard summary for a date range.
   */
  SummaryResponse summary(LocalDate from, LocalDate to);

  /**
   * List recent expense transactions.
   *
   * <p>Implementation should cap the maximum limit to protect the system.
   */
  List<RecentExpenseItem> recentExpenses(int limit);

  /**
   * Expense totals grouped by category for a date range.
   */
  ByCategoryResponse expenseByCategory(LocalDate from, LocalDate to);

  /**
   * Daily expense totals for a date range.
   */
  DailyTrendResponse dailyExpenseTrend(LocalDate from, LocalDate to);
}
