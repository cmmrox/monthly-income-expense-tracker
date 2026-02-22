package com.cmm.mit.service;

import com.cmm.mit.dto.ByCategoryResponse;
import com.cmm.mit.dto.DailyTrendResponse;
import com.cmm.mit.dto.RecentExpenseItem;
import com.cmm.mit.dto.SummaryResponse;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

  SummaryResponse summary(LocalDate from, LocalDate to);

  List<RecentExpenseItem> recentExpenses(int limit);

  ByCategoryResponse expenseByCategory(LocalDate from, LocalDate to);

  DailyTrendResponse dailyExpenseTrend(LocalDate from, LocalDate to);
}
