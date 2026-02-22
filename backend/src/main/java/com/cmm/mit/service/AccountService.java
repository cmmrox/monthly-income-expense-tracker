package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.dto.AccountResponse;
import com.cmm.mit.dto.CreateAccountRequest;
import com.cmm.mit.dto.UpdateAccountRequest;
import java.util.List;
import java.util.UUID;

/**
 * Account service API.
 *
 * <p>Business rules and orchestration for accounts live here (not in controllers).
 */
public interface AccountService {

  /**
   * List active accounts ordered by name.
   */
  List<AccountResponse> listActive();

  /**
   * Create a new account.
   *
   * <p>Implementation sets default flags (e.g., active=true) and persists the entity.
   */
  AccountResponse create(CreateAccountRequest request);

  /**
   * Update an existing account.
   *
   * @throws com.cmm.mit.exception.NotFoundException when the account does not exist
   */
  AccountResponse update(UUID accountId, UpdateAccountRequest request);

  /**
   * Soft-delete (deactivate) an account.
   *
   * @throws com.cmm.mit.exception.NotFoundException when the account does not exist
   */
  void delete(UUID accountId);

  /**
   * Fetch an account entity for internal orchestration (e.g., transaction creation).
   *
   * <p>Avoid using from controllers.
   *
   * @throws com.cmm.mit.exception.NotFoundException when the account does not exist
   */
  Account getEntity(UUID accountId);
}
