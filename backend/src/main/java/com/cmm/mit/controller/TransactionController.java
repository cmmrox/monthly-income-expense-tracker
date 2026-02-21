package com.cmm.mit.controller;

import com.cmm.mit.dto.ApiEnvelope;
import com.cmm.mit.dto.TransactionDtos;
import com.cmm.mit.service.TxnService;
import jakarta.validation.Valid;
import java.time.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TxnService service;

  @PostMapping
  public ApiEnvelope<TransactionDtos.TxnResponse> create(@Valid @RequestBody TransactionDtos.CreateTxnRequest req) {
    var t = service.createIncomeOrExpense(req.txnDate(), req.type(), req.amount(), req.accountId(), req.categoryId(), req.description(), req.merchant(), req.paymentMethod());
    return ApiEnvelope.ok(toResponse(t));
  }

  @PostMapping("/transfer")
  public ApiEnvelope<TransactionDtos.TxnResponse> transfer(@Valid @RequestBody TransactionDtos.CreateTransferRequest req) {
    var t = service.createTransfer(req.txnDate(), req.amount(), req.fromAccountId(), req.toAccountId(), req.description());
    return ApiEnvelope.ok(toResponse(t));
  }

  @GetMapping
  public ApiEnvelope<TransactionDtos.PageResponse<TransactionDtos.TxnResponse>> list(
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
    var p = service.search(from, to, type, accountId, categoryId, pageable);
    var body = new TransactionDtos.PageResponse<>(
        p.getContent().stream().map(TransactionController::toResponse).toList(),
        p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    return ApiEnvelope.ok(body);
  }

  @GetMapping("/{id}")
  public ApiEnvelope<TransactionDtos.TxnResponse> get(@PathVariable UUID id) {
    return ApiEnvelope.ok(toResponse(service.get(id)));
  }

  @DeleteMapping("/{id}")
  public ApiEnvelope<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ApiEnvelope.ok(java.util.Map.of("ok", true));
  }

  static TransactionDtos.TxnResponse toResponse(com.cmm.mit.domain.entity.Txn t) {
    return new TransactionDtos.TxnResponse(
        t.getId(),
        t.getTxnDate(),
        t.getType(),
        t.getAmount(),
        AccountController.toRef(t.getAccount()),
        CategoryController.toRef(t.getCategory()),
        AccountController.toRef(t.getFromAccount()),
        AccountController.toRef(t.getToAccount()),
        t.getDescription(),
        t.getMerchant(),
        t.getPaymentMethod(),
        t.getCreatedAt(),
        t.getUpdatedAt()
    );
  }
}
