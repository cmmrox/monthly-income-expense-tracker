package com.cmm.mit.service.impl;

import com.cmm.mit.domain.entity.Txn;
import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.TransactionDtos;
import com.cmm.mit.exception.BadRequestException;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.mapper.TxnMapper;
import com.cmm.mit.repo.TxnRepo;
import com.cmm.mit.service.AccountService;
import com.cmm.mit.service.CategoryService;
import com.cmm.mit.service.TxnService;
import com.cmm.mit.util.LogSanitizer;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TxnServiceImpl implements TxnService {

  private final TxnRepo repo;
  private final AccountService accountService;
  private final CategoryService categoryService;
  private final TxnMapper mapper;

  @Override
  @Transactional
  public TransactionDtos.TxnResponse create(TransactionDtos.CreateTxnRequest request) {
    log.info("TxnService.create(request={}) start", LogSanitizer.safe(request));

    if (request.type() == TransactionType.TRANSFER) {
      throw new BadRequestException("Use /transactions/transfer for transfers");
    }
    if (request.accountId() == null || request.categoryId() == null) {
      throw new BadRequestException("accountId and categoryId are required for income/expense");
    }

    var account = accountService.getEntity(request.accountId());
    var category = categoryService.getEntity(request.categoryId());

    Txn txn = Txn.builder()
        .txnDate(request.txnDate())
        .type(request.type())
        .amount(request.amount())
        .account(account)
        .category(category)
        .description(request.description())
        .merchant(request.merchant())
        .paymentMethod(request.paymentMethod())
        .build();

    Txn saved = repo.save(txn);
    TransactionDtos.TxnResponse response = mapper.toResponse(saved);

    log.info("TxnService.create(...) end: txnId={}", response.id());
    return response;
  }

  @Override
  @Transactional
  public TransactionDtos.TxnResponse transfer(TransactionDtos.CreateTransferRequest request) {
    log.info("TxnService.transfer(request={}) start", LogSanitizer.safe(request));

    if (request.fromAccountId().equals(request.toAccountId())) {
      throw new BadRequestException("fromAccountId and toAccountId must be different");
    }

    var from = accountService.getEntity(request.fromAccountId());
    var to = accountService.getEntity(request.toAccountId());

    Txn txn = Txn.builder()
        .txnDate(request.txnDate())
        .type(TransactionType.TRANSFER)
        .amount(request.amount())
        .fromAccount(from)
        .toAccount(to)
        .description(request.description())
        .build();

    Txn saved = repo.save(txn);
    TransactionDtos.TxnResponse response = mapper.toResponse(saved);

    log.info("TxnService.transfer(...) end: txnId={}", response.id());
    return response;
  }

  @Override
  public TransactionDtos.TxnResponse get(UUID txnId) {
    log.info("TxnService.get(txnId={}) start", txnId);
    Txn txn = repo.findById(txnId).orElseThrow(() -> new NotFoundException("Transaction not found"));
    TransactionDtos.TxnResponse response = mapper.toResponse(txn);
    log.info("TxnService.get(...) end: txnId={}", response.id());
    return response;
  }

  @Override
  public TransactionDtos.PageResponse<TransactionDtos.TxnResponse> search(
      Instant from,
      Instant to,
      TransactionType type,
      UUID accountId,
      UUID categoryId,
      Pageable pageable) {

    log.info("TxnService.search(from={}, to={}, type={}, accountId={}, categoryId={}, pageable={}) start",
        from, to, type, accountId, categoryId, LogSanitizer.safe(pageable));

    var page = repo.search(from, to, type, accountId, categoryId, pageable);

    var items = page.getContent().stream().map(mapper::toResponse).toList();
    var response = new TransactionDtos.PageResponse<>(
        items,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());

    log.info("TxnService.search(...) end: items={}, page={}, totalItems={}", items.size(), page.getNumber(), page.getTotalElements());
    return response;
  }

  @Override
  @Transactional
  public void delete(UUID txnId) {
    log.info("TxnService.delete(txnId={}) start", txnId);
    Txn txn = repo.findById(txnId).orElseThrow(() -> new NotFoundException("Transaction not found"));
    repo.delete(txn);
    log.info("TxnService.delete(...) end: txnId={}", txnId);
  }
}
