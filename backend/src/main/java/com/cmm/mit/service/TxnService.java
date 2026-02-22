package com.cmm.mit.service;

import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.CreateTransferRequest;
import com.cmm.mit.dto.CreateTxnRequest;
import com.cmm.mit.dto.PageResponse;
import com.cmm.mit.dto.TxnResponse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface TxnService {

  TxnResponse create(CreateTxnRequest request);

  TxnResponse transfer(CreateTransferRequest request);

  TxnResponse get(UUID txnId);

  PageResponse<TxnResponse> search(
      Instant from,
      Instant to,
      TransactionType type,
      UUID accountId,
      UUID categoryId,
      Pageable pageable);

  void delete(UUID txnId);
}
