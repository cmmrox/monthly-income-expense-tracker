package com.cmm.mit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.entity.Txn;
import com.cmm.mit.domain.enums.AccountType;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.*;
import com.cmm.mit.mapper.AccountMapper;
import com.cmm.mit.mapper.CategoryMapper;
import com.cmm.mit.repo.AccountRepo;
import com.cmm.mit.repo.CategoryRepo;
import com.cmm.mit.repo.TxnRepo;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

  @Mock TxnRepo txnRepo;
  @Mock AccountRepo accountRepo;
  @Mock CategoryRepo categoryRepo;
  @Mock AccountMapper accountMapper;
  @Mock CategoryMapper categoryMapper;

  @InjectMocks DashboardServiceImpl service;

  @Test
  void summary_shouldBuildRollupsAndNetTotal() {
    LocalDate from = LocalDate.of(2026, 1, 1);
    LocalDate to = LocalDate.of(2026, 1, 31);

    UUID accId = UUID.randomUUID();
    var account = Account.builder().id(accId).name("Bank").type(AccountType.BANK).currency("LKR").active(true).build();

    when(txnRepo.sumByType(any(), any(), eq(TransactionType.INCOME))).thenReturn(new BigDecimal("100.00"));
    when(txnRepo.sumByType(any(), any(), eq(TransactionType.EXPENSE))).thenReturn(new BigDecimal("40.00"));
    when(accountRepo.findAllByActiveTrueOrderByNameAsc()).thenReturn(List.of(account));

    // One expense txn against the account
    var expenseTxn = Txn.builder().id(UUID.randomUUID()).type(TransactionType.EXPENSE).amount(new BigDecimal("10.00")).account(account)
        .txnDate(Instant.parse("2026-01-10T00:00:00Z")).build();

    when(txnRepo.searchNoAccount(any(), any(), isNull(), isNull(), eq(PageRequest.of(0, 5000))))
        .thenReturn(new PageImpl<>(List.of(expenseTxn)));

    when(accountMapper.toRef(account)).thenReturn(new AccountRef(accId, "Bank", AccountType.BANK));

    SummaryResponse response = service.summary(from, to);

    assertThat(response.incomeTotal()).isEqualByComparingTo("100.00");
    assertThat(response.expenseTotal()).isEqualByComparingTo("40.00");
    assertThat(response.netTotal()).isEqualByComparingTo("60.00");
    assertThat(response.byAccount()).hasSize(1);
    assertThat(response.byAccount().getFirst().account().id()).isEqualTo(accId);
  }

  @Test
  void recentExpenses_shouldCapLimitAt50() {
    when(txnRepo.findRecentExpenses(any())).thenReturn(List.of());

    service.recentExpenses(999);

    verify(txnRepo).findRecentExpenses(eq(PageRequest.of(0, 50)));
  }

  @Test
  void expenseByCategory_shouldComputePercents() {
    LocalDate from = LocalDate.of(2026, 1, 1);
    LocalDate to = LocalDate.of(2026, 1, 31);

    UUID catId = UUID.randomUUID();
    when(txnRepo.sumExpenseByCategory(any(), any())).thenReturn(List.<Object[]>of(new Object[]{catId, new BigDecimal("25.00")}));

    var category = Category.builder().id(catId).name("Groceries").type(CategoryType.EXPENSE).color("#fff").build();
    when(categoryRepo.findAllById(List.of(catId))).thenReturn(List.of(category));
    when(categoryMapper.toRef(category)).thenReturn(new CategoryRef(catId, "Groceries", CategoryType.EXPENSE, "#fff"));

    ByCategoryResponse response = service.expenseByCategory(from, to);

    assertThat(response.items()).hasSize(1);
    assertThat(response.items().getFirst().percent()).isEqualTo(100.0);
  }
}
