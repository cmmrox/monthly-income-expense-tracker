package com.cmm.mit.service;

import com.cmm.mit.dto.DashboardDtos;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

  DashboardDtos.SummaryResponse summary(LocalDate from, LocalDate to);

  List<DashboardDtos.RecentExpenseItem> recentExpenses(int limit);

  DashboardDtos.ByCategoryResponse expenseByCategory(LocalDate from, LocalDate to);

  DashboardDtos.DailyTrendResponse dailyExpenseTrend(LocalDate from, LocalDate to);
}
