package com.cmm.mit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.entity.Txn;
import com.cmm.mit.domain.enums.AccountType;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.*;
import com.cmm.mit.exception.BadRequestException;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.mapper.TxnMapper;
import com.cmm.mit.repo.TxnRepo;
import com.cmm.mit.service.AccountService;
import com.cmm.mit.service.CategoryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TxnServiceImplTest {

  @Mock TxnRepo repo;
  @Mock AccountService accountService;
  @Mock CategoryService categoryService;
  @Mock TxnMapper mapper;

  @InjectMocks TxnServiceImpl service;

  @Captor ArgumentCaptor<Txn> txnCaptor;

  @Test
  void create_whenTransferType_shouldThrowBadRequest() {
    var request = new CreateTxnRequest(Instant.now(), TransactionType.TRANSFER, BigDecimal.ONE, UUID.randomUUID(), UUID.randomUUID(), null, null, null);

    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("transfer");

    verifyNoInteractions(repo);
  }

  @Test
  void create_whenMissingAccountOrCategory_shouldThrowBadRequest() {
    var request = new CreateTxnRequest(Instant.now(), TransactionType.EXPENSE, BigDecimal.ONE, null, null, null, null, null);

    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("accountId");
  }

  @Test
  void create_shouldSaveIncomeOrExpenseTxn() {
    UUID accountId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();

    var request = new CreateTxnRequest(Instant.parse("2026-01-01T00:00:00Z"), TransactionType.EXPENSE, BigDecimal.TEN,
        accountId, categoryId, "desc", "m", "card");

    var account = Account.builder().id(accountId).name("Bank").type(AccountType.BANK).currency("LKR").active(true).build();
    var category = Category.builder().id(categoryId).name("Groceries").type(CategoryType.EXPENSE).active(true).build();

    when(accountService.getEntity(accountId)).thenReturn(account);
    when(categoryService.getEntity(categoryId)).thenReturn(category);
    when(repo.save(any(Txn.class))).thenAnswer(inv -> {
      Txn t = inv.getArgument(0);
      t.setId(UUID.randomUUID());
      return t;
    });

    when(mapper.toResponse(any(Txn.class))).thenAnswer(inv -> {
      Txn t = inv.getArgument(0);
      return new TxnResponse(t.getId(), t.getTxnDate(), t.getType(), t.getAmount(), null, null, null, null, t.getDescription(), t.getMerchant(), t.getPaymentMethod(), null, null);
    });

    TxnResponse response = service.create(request);

    verify(repo).save(txnCaptor.capture());
    assertThat(txnCaptor.getValue().getAccount()).isSameAs(account);
    assertThat(txnCaptor.getValue().getCategory()).isSameAs(category);
    assertThat(response.id()).isNotNull();
  }

  @Test
  void transfer_whenSameAccount_shouldThrowBadRequest() {
    UUID id = UUID.randomUUID();
    var request = new CreateTransferRequest(Instant.now(), BigDecimal.ONE, id, id, "x");

    assertThatThrownBy(() -> service.transfer(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("different");
  }

  @Test
  void get_whenMissing_shouldThrowNotFound() {
    UUID id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.get(id))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void search_shouldReturnPageResponse() {
    Instant from = Instant.parse("2026-01-01T00:00:00Z");
    Instant to = Instant.parse("2026-02-01T00:00:00Z");

    var txn = Txn.builder().id(UUID.randomUUID()).txnDate(from).type(TransactionType.INCOME).amount(BigDecimal.ONE).build();
    when(repo.searchNoAccount(eq(from), eq(to), isNull(), isNull(), any()))
        .thenReturn(new PageImpl<>(List.of(txn), PageRequest.of(0, 20), 1));
    when(mapper.toResponse(txn)).thenReturn(new TxnResponse(txn.getId(), txn.getTxnDate(), txn.getType(), txn.getAmount(), null, null, null, null, null, null, null, null, null));

    PageResponse<TxnResponse> response = service.search(from, to, null, null, null, PageRequest.of(0, 20));

    assertThat(response.items()).hasSize(1);
    assertThat(response.totalItems()).isEqualTo(1);
  }
}
