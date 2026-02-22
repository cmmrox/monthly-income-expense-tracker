package com.cmm.mit.service;

import java.time.*;

/**
 * Salary-cycle period calculations.
 */
public interface PeriodService {

  /**
   * Calculate the current salary-cycle period.
   *
   * <p>Period starts on {@code periodStartDay} each month (e.g. 25) and ends the day before next start.
   */
  Period currentPeriod(Clock clock);

  /**
   * Period representation.
   *
   * @param start first day in the period (inclusive)
   * @param end last day in the period (inclusive)
   * @param startDay configured period start day (1..28)
   */
  record Period(LocalDate start, LocalDate end, int startDay) {
    /**
     * Convert start date to a UTC instant at start of day (inclusive).
     */
    public Instant startInclusiveUtc() {
      return start.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    /**
     * Convert end date to an exclusive end instant: (end + 1 day) at start of day UTC.
     */
    public Instant endExclusiveUtc() {
      return end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
  }
}
