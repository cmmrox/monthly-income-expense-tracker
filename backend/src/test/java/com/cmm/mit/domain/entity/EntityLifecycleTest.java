package com.cmm.mit.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.cmm.mit.domain.enums.AccountType;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.domain.enums.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EntityLifecycleTest {

  @Test
  void account_prePersist_setsDefaults() {
    Account a = new Account();
    a.setName("Cash");
    a.setType(AccountType.CASH);
    a.setCurrency("USD");
    a.setActive(true);

    a.prePersist();

    assertThat(a.getOpeningBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(a.getCreatedAt()).isNotNull();
    assertThat(a.getUpdatedAt()).isNotNull();
  }

  @Test
  void account_preUpdate_updatesTimestamp() throws Exception {
    Account a = new Account();
    a.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));

    a.preUpdate();

    assertThat(a.getUpdatedAt()).isAfter(Instant.parse("2026-01-01T00:00:00Z"));
  }

  @Test
  void category_prePersist_setsTimestamps() {
    Category c = new Category();
    c.setName("Groceries");
    c.setType(CategoryType.EXPENSE);
    c.setActive(true);

    c.prePersist();

    assertThat(c.getCreatedAt()).isNotNull();
    assertThat(c.getUpdatedAt()).isNotNull();
  }

  @Test
  void category_preUpdate_updatesTimestamp() {
    Category c = new Category();
    c.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));

    c.preUpdate();

    assertThat(c.getUpdatedAt()).isAfter(Instant.parse("2026-01-01T00:00:00Z"));
  }

  @Test
  void settings_prePersist_setsDefaults() {
    UserSettings s = new UserSettings();
    s.setBaseCurrency("USD");
    s.setPeriodStartDay(10);

    s.prePersist();

    assertThat(s.getCreatedAt()).isNotNull();
    assertThat(s.getUpdatedAt()).isNotNull();
  }

  @Test
  void settings_preUpdate_updatesTimestamp() {
    UserSettings s = new UserSettings();
    s.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));

    s.preUpdate();

    assertThat(s.getUpdatedAt()).isAfter(Instant.parse("2026-01-01T00:00:00Z"));
  }

  @Test
  void txn_prePersist_setsTimestamps() {
    Txn t = new Txn();
    t.setTxnDate(Instant.parse("2026-02-01T00:00:00Z"));
    t.setType(TransactionType.EXPENSE);
    t.setAmount(new BigDecimal("1.00"));
    t.setId(UUID.randomUUID());

    t.prePersist();

    assertThat(t.getCreatedAt()).isNotNull();
    assertThat(t.getUpdatedAt()).isNotNull();
  }

  @Test
  void txn_preUpdate_updatesTimestamp() {
    Txn t = new Txn();
    t.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));

    t.preUpdate();

    assertThat(t.getUpdatedAt()).isAfter(Instant.parse("2026-01-01T00:00:00Z"));
  }
}
