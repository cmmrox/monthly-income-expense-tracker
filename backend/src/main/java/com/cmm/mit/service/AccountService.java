package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.dto.AccountResponse;
import com.cmm.mit.dto.CreateAccountRequest;
import com.cmm.mit.dto.UpdateAccountRequest;
import java.util.List;
import java.util.UUID;

public interface AccountService {

  List<AccountResponse> listActive();

  AccountResponse create(CreateAccountRequest request);

  AccountResponse update(UUID accountId, UpdateAccountRequest request);

  void delete(UUID accountId);

  /** Internal use (e.g., Txn creation). Avoid using from controllers. */
  Account getEntity(UUID accountId);
}
