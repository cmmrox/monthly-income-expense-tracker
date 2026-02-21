package com.cmm.mit.service;

import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.repo.AccountRepo;
import com.cmm.mit.repo.CategoryRepo;
import com.cmm.mit.repo.TxnRepo;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final TxnRepo txnRepo;
  private final AccountRepo accountRepo;
  private final CategoryRepo categoryRepo;

  public Summary summary(Instant from, Instant to) {
    BigDecimal income = txnRepo.sumByType(from, to, TransactionType.INCOME);
    BigDecimal expense = txnRepo.sumByType(from, to, TransactionType.EXPENSE);

    var accounts = accountRepo.findAllByActiveTrueOrderByNameAsc();
    var totals = new HashMap<UUID, AccountTotals>();
    for (var a : accounts) {
      totals.put(a.getId(), AccountTotals.empty());
    }

    var txns = txnRepo.search(from, to, null, null, null, PageRequest.of(0, 5000)).getContent();
    for (var t : txns) {
      if (t.getType() == TransactionType.INCOME && t.getAccount() != null) {
        var at = totals.getOrDefault(t.getAccount().getId(), AccountTotals.empty());
        totals.put(t.getAccount().getId(), at.addIncome(t.getAmount()));
      } else if (t.getType() == TransactionType.EXPENSE && t.getAccount() != null) {
        var at = totals.getOrDefault(t.getAccount().getId(), AccountTotals.empty());
        totals.put(t.getAccount().getId(), at.addExpense(t.getAmount()));
      } else if (t.getType() == TransactionType.TRANSFER) {
        if (t.getFromAccount() != null) {
          var at = totals.getOrDefault(t.getFromAccount().getId(), AccountTotals.empty());
          totals.put(t.getFromAccount().getId(), at.addTransferOut(t.getAmount()));
        }
        if (t.getToAccount() != null) {
          var at = totals.getOrDefault(t.getToAccount().getId(), AccountTotals.empty());
          totals.put(t.getToAccount().getId(), at.addTransferIn(t.getAmount()));
        }
      }
    }

    return new Summary(income, expense, accounts, totals);
  }

  public List<com.cmm.mit.domain.entity.Txn> recentExpenses(int limit) {
    return txnRepo.findRecentExpenses(PageRequest.of(0, limit));
  }

  public List<CategorySum> expenseByCategory(Instant from, Instant to) {
    var sums = txnRepo.sumExpenseByCategory(from, to);
    var categories = categoryRepo.findAllById(sums.stream().map(o -> (UUID) o[0]).toList());
    var byId = new HashMap<UUID, com.cmm.mit.domain.entity.Category>();
    for (var c : categories) byId.put(c.getId(), c);

    var result = new ArrayList<CategorySum>();
    for (var row : sums) {
      UUID categoryId = (UUID) row[0];
      BigDecimal total = (BigDecimal) row[1];
      result.add(new CategorySum(byId.get(categoryId), total));
    }
    return result;
  }

  public List<DailySum> dailyExpenseTrend(Instant from, Instant to) {
    var rows = txnRepo.dailyExpenseTrend(from, to);
    var result = new ArrayList<DailySum>();
    for (var r : rows) {
      LocalDate date = (LocalDate) r[0];
      BigDecimal total = (BigDecimal) r[1];
      result.add(new DailySum(date, total));
    }
    return result;
  }

  public record AccountTotals(BigDecimal income, BigDecimal expense, BigDecimal transfersOut, BigDecimal transfersIn) {
    public AccountTotals {
      if (income == null) income = BigDecimal.ZERO;
      if (expense == null) expense = BigDecimal.ZERO;
      if (transfersOut == null) transfersOut = BigDecimal.ZERO;
      if (transfersIn == null) transfersIn = BigDecimal.ZERO;
    }

    public static AccountTotals empty() {
      return new AccountTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public AccountTotals addIncome(BigDecimal a) {
      return new AccountTotals(income.add(a), expense, transfersOut, transfersIn);
    }

    public AccountTotals addExpense(BigDecimal a) {
      return new AccountTotals(income, expense.add(a), transfersOut, transfersIn);
    }

    public AccountTotals addTransferOut(BigDecimal a) {
      return new AccountTotals(income, expense, transfersOut.add(a), transfersIn);
    }

    public AccountTotals addTransferIn(BigDecimal a) {
      return new AccountTotals(income, expense, transfersOut, transfersIn.add(a));
    }
  }

  public record Summary(BigDecimal incomeTotal,
                        BigDecimal expenseTotal,
                        List<com.cmm.mit.domain.entity.Account> accounts,
                        Map<UUID, AccountTotals> byAccount) {}
  public record CategorySum(com.cmm.mit.domain.entity.Category category, BigDecimal total) {}
  public record DailySum(LocalDate date, BigDecimal total) {}
}
