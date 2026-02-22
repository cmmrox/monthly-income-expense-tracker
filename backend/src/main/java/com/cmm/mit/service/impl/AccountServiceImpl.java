package com.cmm.mit.service.impl;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.dto.AccountResponse;
import com.cmm.mit.dto.CreateAccountRequest;
import com.cmm.mit.dto.UpdateAccountRequest;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.mapper.AccountMapper;
import com.cmm.mit.repo.AccountRepo;
import com.cmm.mit.service.AccountService;
import com.cmm.mit.util.LogSanitizer;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepo repo;
  private final AccountMapper mapper;

  @Override
  public List<AccountResponse> listActive() {
    log.info("AccountService.listActive() start");
    List<AccountResponse> result = repo.findAllByActiveTrueOrderByNameAsc().stream()
        .map(mapper::toResponse)
        .toList();
    log.info("AccountService.listActive() end: count={}", result.size());
    return result;
  }

  @Override
  @Transactional
  public AccountResponse create(CreateAccountRequest request) {
    log.info("AccountService.create(request={}) start", LogSanitizer.safe(request));

    Account account = mapper.toEntity(request);
    account.setActive(true);

    Account saved = repo.save(account);
    AccountResponse response = mapper.toResponse(saved);

    log.info("AccountService.create(...) end: accountId={}", response.id());
    return response;
  }

  @Override
  @Transactional
  public AccountResponse update(UUID accountId, UpdateAccountRequest request) {
    log.info("AccountService.update(accountId={}, request={}) start", accountId, LogSanitizer.safe(request));

    Account account = getEntity(accountId);
    mapper.updateEntity(request, account);

    Account saved = repo.save(account);
    AccountResponse response = mapper.toResponse(saved);

    log.info("AccountService.update(...) end: accountId={}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(UUID accountId) {
    log.info("AccountService.delete(accountId={}) start", accountId);

    Account account = getEntity(accountId);
    account.setActive(false);
    repo.save(account);

    log.info("AccountService.delete(...) end: accountId={}", accountId);
  }

  @Override
  public Account getEntity(UUID accountId) {
    log.info("AccountService.getEntity(accountId={}) start", accountId);
    Account account = repo.findById(accountId)
        .orElseThrow(() -> new NotFoundException("Account not found"));
    log.info("AccountService.getEntity(...) end: accountId={}", account.getId());
    return account;
  }
}
