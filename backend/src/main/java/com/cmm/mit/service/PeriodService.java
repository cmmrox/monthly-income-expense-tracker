package com.cmm.mit.service;

import java.time.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PeriodService {

  private final SettingsService settingsService;

  /** Salary-cycle period: start on periodStartDay each month (e.g. 25) and end the day before next start. */
  public Period currentPeriod(Clock clock) {
    var settings = settingsService.getOrCreate();
    int startDay = settings.getPeriodStartDay();

    LocalDate today = LocalDate.now(clock);

    LocalDate startThisMonth = LocalDate.of(today.getYear(), today.getMonth(),
        Math.min(startDay, today.lengthOfMonth()));

    LocalDate start;
    if (!today.isBefore(startThisMonth)) {
      start = startThisMonth;
    } else {
      LocalDate prevMonth = today.minusMonths(1);
      start = LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), Math.min(startDay, prevMonth.lengthOfMonth()));
    }

    LocalDate nextStart = start.plusMonths(1);
    nextStart = LocalDate.of(nextStart.getYear(), nextStart.getMonth(), Math.min(startDay, nextStart.lengthOfMonth()));

    LocalDate end = nextStart.minusDays(1);
    return new Period(start, end, startDay);
  }

  public record Period(LocalDate start, LocalDate end, int startDay) {
    public Instant startInclusiveUtc() {
      return start.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    /** exclusive end instant = (end + 1 day) at start of day UTC */
    public Instant endExclusiveUtc() {
      return end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
  }
}
