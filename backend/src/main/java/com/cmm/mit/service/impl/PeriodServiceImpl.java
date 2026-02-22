package com.cmm.mit.service.impl;

import com.cmm.mit.service.PeriodService;
import com.cmm.mit.service.SettingsService;
import com.cmm.mit.util.LogSanitizer;
import java.time.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Period service implementation.
 *
 * <p>Computes salary-cycle periods based on the configured start day (1..28).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodServiceImpl implements PeriodService {

  private final SettingsService settingsService;

  /**
   * {@inheritDoc}
   */
  @Override
  public Period currentPeriod(Clock clock) {
    log.info("PeriodService.currentPeriod(clock={}) start", LogSanitizer.safe(clock));

    var settings = settingsService.getOrCreate();
    int startDay = settings.getPeriodStartDay();

    LocalDate today = LocalDate.now(clock);

    // Candidate start date for the current month. We cap the day to the month length.
    LocalDate startThisMonth = LocalDate.of(today.getYear(), today.getMonth(),
        Math.min(startDay, today.lengthOfMonth()));

    // If today is on/after the candidate start date, we start this month.
    // Otherwise, the current salary-cycle period started last month.
    LocalDate start;
    if (!today.isBefore(startThisMonth)) {
      start = startThisMonth;
    } else {
      LocalDate prevMonth = today.minusMonths(1);
      start = LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), Math.min(startDay, prevMonth.lengthOfMonth()));
    }

    // Next cycle starts one month after start, again capped by month length.
    LocalDate nextStart = start.plusMonths(1);
    nextStart = LocalDate.of(nextStart.getYear(), nextStart.getMonth(), Math.min(startDay, nextStart.lengthOfMonth()));

    // End date is inclusive; it is the day before nextStart.
    LocalDate end = nextStart.minusDays(1);
    Period period = new Period(start, end, startDay);

    log.info("PeriodService.currentPeriod(...) end: start={}, end={}, startDay={}", period.start(), period.end(), period.startDay());
    return period;
  }
}
