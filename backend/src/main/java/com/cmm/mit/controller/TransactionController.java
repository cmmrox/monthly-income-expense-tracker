package com.cmm.mit.controller;

import com.cmm.mit.dto.CreateTransferRequest;
import com.cmm.mit.dto.CreateTxnRequest;
import com.cmm.mit.dto.PageResponse;
import com.cmm.mit.dto.TxnResponse;
import com.cmm.mit.service.TxnService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TxnService service;

  @PostMapping
  public ResponseEntity<TxnResponse> create(@Valid @RequestBody CreateTxnRequest request) {
    return ResponseEntity.ok(service.create(request));
  }

  @PostMapping("/transfer")
  public ResponseEntity<TxnResponse> transfer(@Valid @RequestBody CreateTransferRequest request) {
    return ResponseEntity.ok(service.transfer(request));
  }

  @GetMapping
  public ResponseEntity<PageResponse<TxnResponse>> list(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(required = false) com.cmm.mit.domain.enums.TransactionType type,
      @RequestParam(required = false) UUID accountId,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "txnDate") String sort,
      @RequestParam(defaultValue = "desc") String dir
  ) {
    var direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    var pageable = PageRequest.of(page, size, Sort.by(direction, sort));
    return ResponseEntity.ok(service.search(from, to, type, accountId, categoryId, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TxnResponse> get(@PathVariable UUID id) {
    return ResponseEntity.ok(service.get(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.ok(java.util.Map.of("ok", true));
  }
}
