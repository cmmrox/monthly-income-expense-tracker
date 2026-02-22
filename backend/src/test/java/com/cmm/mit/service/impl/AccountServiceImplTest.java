package com.cmm.mit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.domain.enums.AccountType;
import com.cmm.mit.dto.AccountResponse;
import com.cmm.mit.dto.CreateAccountRequest;
import com.cmm.mit.dto.UpdateAccountRequest;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.mapper.AccountMapper;
import com.cmm.mit.repo.AccountRepo;
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

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @Mock AccountRepo repo;
  @Mock AccountMapper mapper;

  @InjectMocks AccountServiceImpl service;

  @Captor ArgumentCaptor<Account> accountCaptor;

  @Test
  void listActive_shouldReturnMappedResponses() {
    var entity = Account.builder().id(UUID.randomUUID()).name("Bank").type(AccountType.BANK)
        .currency("LKR").openingBalance(BigDecimal.ZERO).active(true)
        .createdAt(Instant.now()).updatedAt(Instant.now()).build();

    when(repo.findAllByActiveTrueOrderByNameAsc()).thenReturn(List.of(entity));
    when(mapper.toResponse(entity)).thenReturn(new AccountResponse(entity.getId(), entity.getName(), entity.getType(), entity.getCurrency(),
        entity.getOpeningBalance(), entity.isActive(), entity.getCreatedAt(), entity.getUpdatedAt()));

    List<AccountResponse> result = service.listActive();

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().id()).isEqualTo(entity.getId());
  }

  @Test
  void create_shouldSetActiveTrueAndSave() {
    var request = new CreateAccountRequest("Bank", AccountType.BANK, "LKR", BigDecimal.TEN);
    var mapped = Account.builder().name("Bank").type(AccountType.BANK).currency("LKR").openingBalance(BigDecimal.TEN).active(false).build();

    when(mapper.toEntity(request)).thenReturn(mapped);
    when(repo.save(any(Account.class))).thenAnswer(inv -> {
      Account a = inv.getArgument(0);
      a.setId(UUID.randomUUID());
      a.setCreatedAt(Instant.now());
      a.setUpdatedAt(Instant.now());
      return a;
    });
    when(mapper.toResponse(any(Account.class))).thenAnswer(inv -> {
      Account a = inv.getArgument(0);
      return new AccountResponse(a.getId(), a.getName(), a.getType(), a.getCurrency(), a.getOpeningBalance(), a.isActive(), a.getCreatedAt(), a.getUpdatedAt());
    });

    AccountResponse response = service.create(request);

    verify(repo).save(accountCaptor.capture());
    assertThat(accountCaptor.getValue().isActive()).isTrue();
    assertThat(response.id()).isNotNull();
  }

  @Test
  void update_shouldApplyMapperUpdateAndSave() {
    UUID id = UUID.randomUUID();
    var existing = Account.builder().id(id).name("Old").type(AccountType.BANK).currency("LKR").openingBalance(BigDecimal.ZERO).active(true)
        .createdAt(Instant.now()).updatedAt(Instant.now()).build();

    var request = new UpdateAccountRequest("New", AccountType.CASH, "USD", BigDecimal.ONE, false);

    when(repo.findById(id)).thenReturn(Optional.of(existing));
    when(repo.save(existing)).thenReturn(existing);
    when(mapper.toResponse(existing)).thenReturn(new AccountResponse(existing.getId(), existing.getName(), existing.getType(), existing.getCurrency(),
        existing.getOpeningBalance(), existing.isActive(), existing.getCreatedAt(), existing.getUpdatedAt()));

    AccountResponse response = service.update(id, request);

    verify(mapper).updateEntity(request, existing);
    verify(repo).save(existing);
    assertThat(response.id()).isEqualTo(id);
  }

  @Test
  void delete_shouldSoftDeleteBySettingActiveFalse() {
    UUID id = UUID.randomUUID();
    var existing = Account.builder().id(id).active(true).build();

    when(repo.findById(id)).thenReturn(Optional.of(existing));
    when(repo.save(any(Account.class))).thenReturn(existing);

    service.delete(id);

    assertThat(existing.isActive()).isFalse();
    verify(repo).save(existing);
  }

  @Test
  void getEntity_whenNotFound_shouldThrow() {
    UUID id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getEntity(id))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Account not found");
  }
}
