package com.cmm.mit.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class PeriodServicePeriodTest {

  @Test
  void startInclusiveUtc_isStartOfDayUtc() {
    var p = new PeriodService.Period(LocalDate.parse("2026-02-25"), LocalDate.parse("2026-03-24"), 25);

    Instant start = p.startInclusiveUtc();

    assertThat(start).isEqualTo(LocalDate.parse("2026-02-25").atStartOfDay(ZoneOffset.UTC).toInstant());
  }

  @Test
  void endExclusiveUtc_isNextDayStartOfDayUtc() {
    var p = new PeriodService.Period(LocalDate.parse("2026-02-25"), LocalDate.parse("2026-03-24"), 25);

    Instant end = p.endExclusiveUtc();

    assertThat(end).isEqualTo(LocalDate.parse("2026-03-25").atStartOfDay(ZoneOffset.UTC).toInstant());
  }
}
