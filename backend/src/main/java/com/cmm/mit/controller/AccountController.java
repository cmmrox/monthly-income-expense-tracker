package com.cmm.mit.controller;

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
    return ApiEnvelope.ok(service.listActive());
  }

  @PostMapping
  public ApiEnvelope<AccountDtos.AccountResponse> create(@Valid @RequestBody AccountDtos.CreateAccountRequest request) {
    return ApiEnvelope.ok(service.create(request));
  }

  @PutMapping("/{id}")
  public ApiEnvelope<AccountDtos.AccountResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody AccountDtos.UpdateAccountRequest request) {

    return ApiEnvelope.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ApiEnvelope<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ApiEnvelope.ok(java.util.Map.of("ok", true));
  }
}
