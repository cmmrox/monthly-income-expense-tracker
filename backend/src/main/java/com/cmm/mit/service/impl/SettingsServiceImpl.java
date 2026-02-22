package com.cmm.mit.service.impl;

import com.cmm.mit.domain.entity.UserSettings;
import com.cmm.mit.exception.BadRequestException;
import com.cmm.mit.repo.UserSettingsRepo;
import com.cmm.mit.service.SettingsService;
import com.cmm.mit.util.LogSanitizer;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

  private final UserSettingsRepo repo;

  @Value("${app.baseCurrency:LKR}")
  String defaultCurrency;

  @Value("${app.periodStartDay:25}")
  int defaultPeriodStartDay;

  @Override
  @Transactional
  public UserSettings getOrCreate() {
    log.info("SettingsService.getOrCreate() start");

    Optional<UserSettings> existing = repo.findAll().stream().findFirst();
    if (existing.isPresent()) {
      log.info("SettingsService.getOrCreate() end: existing=true");
      return existing.get();
    }

    validatePeriodStartDay(defaultPeriodStartDay);

    var settings = UserSettings.builder()
        .baseCurrency(defaultCurrency)
        .periodStartDay(defaultPeriodStartDay)
        .build();

    UserSettings saved = repo.save(settings);
    log.info("SettingsService.getOrCreate() end: created=true, baseCurrency={}, periodStartDay={}",
        LogSanitizer.safe(saved.getBaseCurrency()), saved.getPeriodStartDay());
    return saved;
  }

  @Override
  @Transactional
  public UserSettings update(String baseCurrency, int periodStartDay) {
    log.info("SettingsService.update(baseCurrency={}, periodStartDay={}) start",
        LogSanitizer.safe(baseCurrency), periodStartDay);

    validatePeriodStartDay(periodStartDay);

    var settings = getOrCreate();
    settings.setBaseCurrency(baseCurrency);
    settings.setPeriodStartDay(periodStartDay);

    UserSettings saved = repo.save(settings);
    log.info("SettingsService.update(...) end: baseCurrency={}, periodStartDay={}",
        LogSanitizer.safe(saved.getBaseCurrency()), saved.getPeriodStartDay());
    return saved;
  }

  private static void validatePeriodStartDay(int periodStartDay) {
    if (periodStartDay < 1 || periodStartDay > 28) {
      throw new BadRequestException("periodStartDay must be between 1 and 28");
    }
  }
}
