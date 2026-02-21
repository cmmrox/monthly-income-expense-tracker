package com.cmm.mit.controller;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.dto.AccountDtos;
import com.cmm.mit.dto.ApiEnvelope;
import com.cmm.mit.service.AccountService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService service;

  @GetMapping
  public ApiEnvelope<java.util.List<AccountDtos.AccountResponse>> list() {
    return ApiEnvelope.ok(service.listActive().stream().map(AccountController::toResponse).toList());
  }

  @PostMapping
  public ApiEnvelope<AccountDtos.AccountResponse> create(@Valid @RequestBody AccountDtos.CreateAccountRequest req) {
    var a = Account.builder()
        .name(req.name())
        .type(req.type())
        .currency(req.currency())
        .openingBalance(req.openingBalance())
        .build();
    return ApiEnvelope.ok(toResponse(service.create(a)));
  }

  @PutMapping("/{id}")
  public ApiEnvelope<AccountDtos.AccountResponse> update(@PathVariable UUID id, @Valid @RequestBody AccountDtos.UpdateAccountRequest req) {
    var patch = Account.builder()
        .name(req.name())
        .type(req.type())
        .currency(req.currency())
        .openingBalance(req.openingBalance())
        .active(req.active())
        .build();
    return ApiEnvelope.ok(toResponse(service.update(id, patch)));
  }

  @DeleteMapping("/{id}")
  public ApiEnvelope<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ApiEnvelope.ok(java.util.Map.of("ok", true));
  }

  static AccountDtos.AccountResponse toResponse(Account a) {
    return new AccountDtos.AccountResponse(
        a.getId(), a.getName(), a.getType(), a.getCurrency(), a.getOpeningBalance(), a.isActive(), a.getCreatedAt(), a.getUpdatedAt());
  }

  public static AccountDtos.AccountRef toRef(Account a) {
    if (a == null) return null;
    return new AccountDtos.AccountRef(a.getId(), a.getName(), a.getType());
  }
}
