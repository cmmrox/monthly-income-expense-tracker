package com.cmm.mit.controller;

import com.cmm.mit.dto.AccountResponse;
import com.cmm.mit.dto.CreateAccountRequest;
import com.cmm.mit.dto.UpdateAccountRequest;
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
  public ResponseEntity<java.util.List<AccountResponse>> list() {
    return ResponseEntity.ok(service.listActive());
  }

  @PostMapping
  public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
    return ResponseEntity.ok(service.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AccountResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateAccountRequest request) {

    return ResponseEntity.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.ok(java.util.Map.of("ok", true));
  }
}
