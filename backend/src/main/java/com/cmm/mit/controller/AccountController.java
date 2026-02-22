package com.cmm.mit.controller;

import com.cmm.mit.dto.AccountDtos;
import com.cmm.mit.service.AccountService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService service;

  @GetMapping
  public ResponseEntity<java.util.List<AccountDtos.AccountResponse>> list() {
    return ResponseEntity.ok(service.listActive());
  }

  @PostMapping
  public ResponseEntity<AccountDtos.AccountResponse> create(@Valid @RequestBody AccountDtos.CreateAccountRequest request) {
    return ResponseEntity.ok(service.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AccountDtos.AccountResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody AccountDtos.UpdateAccountRequest request) {

    return ResponseEntity.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.ok(java.util.Map.of("ok", true));
  }
}
