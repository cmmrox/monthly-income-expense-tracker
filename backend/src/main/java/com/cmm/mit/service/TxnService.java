package com.cmm.mit.service;

import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.CreateTransferRequest;
import com.cmm.mit.dto.CreateTxnRequest;
import com.cmm.mit.dto.PageResponse;
import com.cmm.mit.dto.TxnResponse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/**
 * Transaction service API.
 *
 * <p>Owns all business rules for income/expense/transfer creation and transaction search.
 */
public interface TxnService {

  /**
   * Create an income or expense transaction.
   *
   * @throws com.cmm.mit.exception.BadRequestException when request is invalid (e.g., missing account/category)
   */
  TxnResponse create(CreateTxnRequest request);

  /**
   * Create a transfer transaction.
   *
   * @throws com.cmm.mit.exception.BadRequestException when from/to accounts are the same
   */
  TxnResponse transfer(CreateTransferRequest request);

  /**
   * Retrieve a transaction by id.
   *
   * @throws com.cmm.mit.exception.NotFoundException when transaction does not exist
   */
  TxnResponse get(UUID txnId);

  /**
   * Search transactions within a date range with optional filters and paging.
   */
  PageResponse<TxnResponse> search(
      Instant from,
      Instant to,
      TransactionType type,
      UUID accountId,
      UUID categoryId,
      Pageable pageable);

  /**
   * Delete a transaction by id.
   *
   * @throws com.cmm.mit.exception.NotFoundException when transaction does not exist
   */
  void delete(UUID txnId);
}
