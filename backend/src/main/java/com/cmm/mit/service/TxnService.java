package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Txn;
import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.exception.BadRequestException;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.repo.TxnRepo;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TxnService {

  private final TxnRepo repo;
  private final AccountService accountService;
  private final CategoryService categoryService;

  @Transactional
  public Txn createIncomeOrExpense(Instant txnDate, TransactionType type, java.math.BigDecimal amount,
                                  UUID accountId, UUID categoryId,
                                  String description, String merchant, String paymentMethod) {

    if (type == TransactionType.TRANSFER) {
      throw new BadRequestException("Use /transactions/transfer for transfers");
    }
    if (accountId == null || categoryId == null) {
      throw new BadRequestException("accountId and categoryId are required for income/expense");
    }

    var account = accountService.get(accountId);
    var category = categoryService.get(categoryId);

    var t = Txn.builder()
        .txnDate(txnDate)
        .type(type)
        .amount(amount)
        .account(account)
        .category(category)
        .description(description)
        .merchant(merchant)
        .paymentMethod(paymentMethod)
        .build();
    return repo.save(t);
  }

  @Transactional
  public Txn createTransfer(Instant txnDate, java.math.BigDecimal amount,
                            UUID fromAccountId, UUID toAccountId, String description) {

    if (fromAccountId.equals(toAccountId)) {
      throw new BadRequestException("fromAccountId and toAccountId must be different");
    }

    var from = accountService.get(fromAccountId);
    var to = accountService.get(toAccountId);

    var t = Txn.builder()
        .txnDate(txnDate)
        .type(TransactionType.TRANSFER)
        .amount(amount)
        .fromAccount(from)
        .toAccount(to)
        .description(description)
        .build();
    return repo.save(t);
  }

  public Txn get(UUID id) {
    return repo.findById(id).orElseThrow(() -> new NotFoundException("Transaction not found"));
  }

  public Page<Txn> search(Instant from, Instant to, TransactionType type, UUID accountId, UUID categoryId, Pageable pageable) {
    return repo.search(from, to, type, accountId, categoryId, pageable);
  }

  @Transactional
  public void delete(UUID id) {
    repo.delete(get(id));
  }
}
