package com.cmm.mit.service;

import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.TransactionDtos;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface TxnService {

  TransactionDtos.TxnResponse create(TransactionDtos.CreateTxnRequest request);

  TransactionDtos.TxnResponse transfer(TransactionDtos.CreateTransferRequest request);

  TransactionDtos.TxnResponse get(UUID txnId);

  TransactionDtos.PageResponse<TransactionDtos.TxnResponse> search(
      Instant from,
      Instant to,
      TransactionType type,
      UUID accountId,
      UUID categoryId,
      Pageable pageable);

  void delete(UUID txnId);
}
