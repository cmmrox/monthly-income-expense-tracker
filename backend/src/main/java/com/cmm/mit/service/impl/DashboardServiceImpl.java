package com.cmm.mit.service.impl;

import com.cmm.mit.common.util.BigDecimalUtils;
import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.AccountRollup;
import com.cmm.mit.dto.ByCategoryItem;
import com.cmm.mit.dto.ByCategoryResponse;
import com.cmm.mit.dto.DailyPoint;
import com.cmm.mit.dto.DailyTrendResponse;
import com.cmm.mit.dto.RecentExpenseItem;
import com.cmm.mit.dto.SummaryResponse;
import com.cmm.mit.mapper.AccountMapper;
import com.cmm.mit.mapper.CategoryMapper;
import com.cmm.mit.repo.AccountRepo;
import com.cmm.mit.repo.CategoryRepo;
import com.cmm.mit.repo.TxnRepo;
import com.cmm.mit.service.DashboardService;
import com.cmm.mit.util.LogSanitizer;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Dashboard/reporting service implementation.
 *
 * <p>Encapsulates the aggregation logic used by the UI. Complex report calculations should be documented to keep
 * maintenance easy.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final TxnRepo txnRepo;
  private final AccountRepo accountRepo;
  private final CategoryRepo categoryRepo;
  private final AccountMapper accountMapper;
  private final CategoryMapper categoryMapper;

  /** {@inheritDoc} */
  @Override
  public SummaryResponse summary(LocalDate from, LocalDate to) {
    log.info("DashboardService.summary(from={}, to={}) start", from, to);

    Instant fromInclusive = from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toExclusive = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    BigDecimal incomeTotal =
        BigDecimalUtils.zeroIfNull(txnRepo.sumByType(fromInclusive, toExclusive, TransactionType.INCOME));
    BigDecimal expenseTotal =
        BigDecimalUtils.zeroIfNull(txnRepo.sumByType(fromInclusive, toExclusive, TransactionType.EXPENSE));

    var accounts = accountRepo.findAllByActiveTrueOrderByNameAsc();

    // Initialize totals map so all active accounts appear in the response (even with zero activity).
    var totalsByAccountId = new HashMap<UUID, AccountTotals>();
    for (var account : accounts) {
      totalsByAccountId.put(account.getId(), AccountTotals.empty());
    }

    // Fetch transactions for the period.
    // Note: this uses a bounded page size as a pragmatic guardrail for now.
    var txns = txnRepo.searchNoAccount(fromInclusive, toExclusive, null, null, PageRequest.of(0, 5000)).getContent();

    // Roll-up totals per account. Transfers affect both from/to accounts.
    for (var txn : txns) {
      if (txn.getType() == TransactionType.INCOME && txn.getAccount() != null) {
        var current = totalsByAccountId.getOrDefault(txn.getAccount().getId(), AccountTotals.empty());
        totalsByAccountId.put(txn.getAccount().getId(), current.addIncome(txn.getAmount()));
      } else if (txn.getType() == TransactionType.EXPENSE && txn.getAccount() != null) {
        var current = totalsByAccountId.getOrDefault(txn.getAccount().getId(), AccountTotals.empty());
        totalsByAccountId.put(txn.getAccount().getId(), current.addExpense(txn.getAmount()));
      } else if (txn.getType() == TransactionType.TRANSFER) {
        if (txn.getFromAccount() != null) {
          var current = totalsByAccountId.getOrDefault(txn.getFromAccount().getId(), AccountTotals.empty());
          totalsByAccountId.put(txn.getFromAccount().getId(), current.addTransferOut(txn.getAmount()));
        }
        if (txn.getToAccount() != null) {
          var current = totalsByAccountId.getOrDefault(txn.getToAccount().getId(), AccountTotals.empty());
          totalsByAccountId.put(txn.getToAccount().getId(), current.addTransferIn(txn.getAmount()));
        }
      }
    }

    var rollups = new ArrayList<AccountRollup>();
    for (var account : accounts) {
      var totals = totalsByAccountId.getOrDefault(account.getId(), AccountTotals.empty());
      rollups.add(
          new AccountRollup(
              accountMapper.toRef(account),
              totals.income(),
              totals.expense(),
              totals.transfersOut(),
              totals.transfersIn()));
    }

    var response =
        new SummaryResponse(
            from, to, incomeTotal, expenseTotal, incomeTotal.subtract(expenseTotal), rollups);

    log.info(
        "DashboardService.summary(...) end: accounts={}, incomeTotal={}, expenseTotal={}",
        rollups.size(),
        incomeTotal,
        expenseTotal);
    return response;
  }

  /** {@inheritDoc} */
  @Override
  public List<RecentExpenseItem> recentExpenses(int limit) {
    // Safety cap to avoid accidental heavy queries.
    int boundedLimit = Math.min(limit, 50);
    log.info("DashboardService.recentExpenses(limit={}) start", boundedLimit);

    var result =
        txnRepo.findRecentExpenses(PageRequest.of(0, boundedLimit)).stream()
            .map(
                txn ->
                    new RecentExpenseItem(
                        txn.getId(),
                        txn.getTxnDate(),
                        txn.getAmount(),
                        categoryMapper.toRef(txn.getCategory()),
                        accountMapper.toRef(txn.getAccount()),
                        txn.getDescription(),
                        txn.getMerchant()))
            .toList();

    log.info("DashboardService.recentExpenses(...) end: count={}", result.size());
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public ByCategoryResponse expenseByCategory(LocalDate from, LocalDate to) {
    log.info("DashboardService.expenseByCategory(from={}, to={}) start", from, to);

    Instant fromInclusive = from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toExclusive = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    var sums = txnRepo.sumExpenseByCategory(fromInclusive, toExclusive);
    // Grand total is used to compute percentages per category.
    BigDecimal grand =
        sums.stream()
            .map(row -> (BigDecimal) row[1])
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    var categories = categoryRepo.findAllById(sums.stream().map(row -> (UUID) row[0]).toList());
    var byId = new HashMap<UUID, com.cmm.mit.domain.entity.Category>();
    for (var category : categories) {
      byId.put(category.getId(), category);
    }

    var items = new ArrayList<ByCategoryItem>();
    for (var row : sums) {
      UUID categoryId = (UUID) row[0];
      BigDecimal total = BigDecimalUtils.zeroIfNull((BigDecimal) row[1]);
      double pct =
          (grand.compareTo(BigDecimal.ZERO) == 0)
              ? 0.0
              : total
                  .multiply(BigDecimal.valueOf(100))
                  .divide(grand, 2, java.math.RoundingMode.HALF_UP)
                  .doubleValue();

      items.add(new ByCategoryItem(categoryMapper.toRef(byId.get(categoryId)), total, pct));
    }

    var response = new ByCategoryResponse(from, to, items);
    log.info(
        "DashboardService.expenseByCategory(...) end: items={}, grandTotal={}",
        items.size(),
        LogSanitizer.safe(grand));
    return response;
  }

  /** {@inheritDoc} */
  @Override
  public DailyTrendResponse dailyExpenseTrend(LocalDate from, LocalDate to) {
    log.info("DashboardService.dailyExpenseTrend(from={}, to={}) start", from, to);

    Instant fromInclusive = from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant toExclusive = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    var points =
        txnRepo.dailyExpenseTrend(fromInclusive, toExclusive).stream()
            .map(row -> new DailyPoint((LocalDate) row[0], BigDecimalUtils.zeroIfNull((BigDecimal) row[1])))
            .toList();

    var response = new DailyTrendResponse(from, to, points);
    log.info("DashboardService.dailyExpenseTrend(...) end: points={}", points.size());
    return response;
  }

  private record AccountTotals(
      BigDecimal income, BigDecimal expense, BigDecimal transfersOut, BigDecimal transfersIn) {
    private static AccountTotals empty() {
      return new AccountTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private AccountTotals addIncome(BigDecimal amount) {
      return new AccountTotals(
          income.add(BigDecimalUtils.zeroIfNull(amount)), expense, transfersOut, transfersIn);
    }

    private AccountTotals addExpense(BigDecimal amount) {
      return new AccountTotals(
          income, expense.add(BigDecimalUtils.zeroIfNull(amount)), transfersOut, transfersIn);
    }

    private AccountTotals addTransferOut(BigDecimal amount) {
      return new AccountTotals(
          income, expense, transfersOut.add(BigDecimalUtils.zeroIfNull(amount)), transfersIn);
    }

    private AccountTotals addTransferIn(BigDecimal amount) {
      return new AccountTotals(
          income, expense, transfersOut, transfersIn.add(BigDecimalUtils.zeroIfNull(amount)));
    }
  }
}
