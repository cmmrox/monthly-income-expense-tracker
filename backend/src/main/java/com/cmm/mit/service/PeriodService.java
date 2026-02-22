package com.cmm.mit.service;

import java.time.*;

public interface PeriodService {

  /** Salary-cycle period: start on periodStartDay each month (e.g. 25) and end the day before next start. */
  Period currentPeriod(Clock clock);

  record Period(LocalDate start, LocalDate end, int startDay) {
    public Instant startInclusiveUtc() {
      return start.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    /** exclusive end instant = (end + 1 day) at start of day UTC */
    public Instant endExclusiveUtc() {
      return end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
  }
}
