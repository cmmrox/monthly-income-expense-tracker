package com.cmm.mit.service.impl;

import com.cmm.mit.service.PeriodService;
import com.cmm.mit.service.SettingsService;
import com.cmm.mit.util.LogSanitizer;
import java.time.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodServiceImpl implements PeriodService {

  private final SettingsService settingsService;

  @Override
  public Period currentPeriod(Clock clock) {
    log.info("PeriodService.currentPeriod(clock={}) start", LogSanitizer.safe(clock));

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
    Period period = new Period(start, end, startDay);

    log.info("PeriodService.currentPeriod(...) end: start={}, end={}, startDay={}", period.start(), period.end(), period.startDay());
    return period;
  }
}
