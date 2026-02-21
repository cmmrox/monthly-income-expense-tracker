package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Account;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.repo.AccountRepo;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final AccountRepo repo;

  public List<Account> listActive() {
    return repo.findAllByActiveTrueOrderByNameAsc();
  }

  @Transactional
  public Account create(Account a) {
    a.setActive(true);
    return repo.save(a);
  }

  public Account get(UUID id) {
    return repo.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
  }

  @Transactional
  public Account update(UUID id, Account patch) {
    var a = get(id);
    a.setName(patch.getName());
    a.setType(patch.getType());
    a.setCurrency(patch.getCurrency());
    a.setOpeningBalance(patch.getOpeningBalance());
    a.setActive(patch.isActive());
    return repo.save(a);
  }

  @Transactional
  public void delete(UUID id) {
    var a = get(id);
    a.setActive(false);
    repo.save(a);
  }
}
