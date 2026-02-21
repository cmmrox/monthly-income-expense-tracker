package com.cmm.mit.repo;

import com.cmm.mit.domain.entity.Account;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account, UUID> {
  List<Account> findAllByActiveTrueOrderByNameAsc();
}
