package com.cmm.mit.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static com.cmm.mit.dto.AccountDtos.*;
import static com.cmm.mit.dto.CategoryDtos.*;

public class DashboardDtos {

  public record PeriodResponse(LocalDate periodStart, LocalDate periodEnd, int periodStartDay) {}

  public record AccountRollup(AccountRef account,
                              BigDecimal income,
                              BigDecimal expense,
                              BigDecimal transfersOut,
                              BigDecimal transfersIn) {}

  public record SummaryResponse(LocalDate from,
                                LocalDate to,
                                BigDecimal incomeTotal,
                                BigDecimal expenseTotal,
                                BigDecimal netTotal,
                                List<AccountRollup> byAccount) {}

  public record RecentExpenseItem(java.util.UUID id,
                                  java.time.Instant txnDate,
                                  BigDecimal amount,
                                  CategoryRef category,
                                  AccountRef account,
                                  String description,
                                  String merchant) {}

  public record ByCategoryItem(CategoryRef category, BigDecimal total, double percent) {}
  public record ByCategoryResponse(LocalDate from, LocalDate to, List<ByCategoryItem> items) {}

  public record DailyPoint(LocalDate date, BigDecimal total) {}
  public record DailyTrendResponse(LocalDate from, LocalDate to, List<DailyPoint> points) {}
}
