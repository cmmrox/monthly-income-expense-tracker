package com.cmm.mit.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PeriodServiceTest {

  @Autowired PeriodService periodService;

  @Test
  void currentPeriod_whenAfterStartDay_shouldStartThisMonth() {
    Clock clock = Clock.fixed(Instant.parse("2026-03-26T00:00:00Z"), ZoneOffset.UTC);
    var p = periodService.currentPeriod(clock);
    assertThat(p.start()).isEqualTo(LocalDate.of(2026, 3, 25));
    assertThat(p.end()).isEqualTo(LocalDate.of(2026, 4, 24));
  }

  @Test
  void currentPeriod_whenBeforeStartDay_shouldStartPreviousMonth() {
    Clock clock = Clock.fixed(Instant.parse("2026-03-01T00:00:00Z"), ZoneOffset.UTC);
    var p = periodService.currentPeriod(clock);
    assertThat(p.start()).isEqualTo(LocalDate.of(2026, 2, 25));
    assertThat(p.end()).isEqualTo(LocalDate.of(2026, 3, 24));
  }
}
