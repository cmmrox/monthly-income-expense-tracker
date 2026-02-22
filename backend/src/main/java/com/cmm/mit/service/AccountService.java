package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.dto.AccountDtos;
import java.util.List;
import java.util.UUID;

public interface AccountService {

  List<AccountDtos.AccountResponse> listActive();

  AccountDtos.AccountResponse create(AccountDtos.CreateAccountRequest request);

  AccountDtos.AccountResponse update(UUID accountId, AccountDtos.UpdateAccountRequest request);

  void delete(UUID accountId);

  /** Internal use (e.g., Txn creation). Avoid using from controllers. */
  Account getEntity(UUID accountId);
}
