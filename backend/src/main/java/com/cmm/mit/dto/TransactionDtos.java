package com.cmm.mit.dto;

import com.cmm.mit.domain.enums.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.cmm.mit.dto.AccountDtos.*;
import static com.cmm.mit.dto.CategoryDtos.*;

public class TransactionDtos {

  public record CreateTxnRequest(
      @NotNull Instant txnDate,
      @NotNull TransactionType type,
      @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
      UUID accountId,
      UUID categoryId,
      @Size(max = 255) String description,
      @Size(max = 120) String merchant,
      @Size(max = 40) String paymentMethod
  ) {}

  public record CreateTransferRequest(
      @NotNull Instant txnDate,
      @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
      @NotNull UUID fromAccountId,
      @NotNull UUID toAccountId,
      @Size(max = 255) String description
  ) {}

  public record TxnResponse(
      UUID id,
      Instant txnDate,
      TransactionType type,
      BigDecimal amount,
      AccountRef account,
      CategoryRef category,
      AccountRef fromAccount,
      AccountRef toAccount,
      String description,
      String merchant,
      String paymentMethod,
      Instant createdAt,
      Instant updatedAt
  ) {}

  public record PageResponse<T>(
      java.util.List<T> items,
      int page,
      int size,
      long totalItems,
      int totalPages
  ) {}
}
