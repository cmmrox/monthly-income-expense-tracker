package com.cmm.mit.service;

import com.cmm.mit.domain.entity.UserSettings;
import com.cmm.mit.exception.BadRequestException;
import com.cmm.mit.repo.UserSettingsRepo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService {

  private final UserSettingsRepo repo;

  @Value("${app.baseCurrency:LKR}")
  String defaultCurrency;

  @Value("${app.periodStartDay:25}")
  int defaultPeriodStartDay;

  @Transactional
  public UserSettings getOrCreate() {
    Optional<UserSettings> existing = repo.findAll().stream().findFirst();
    if (existing.isPresent()) return existing.get();

    if (defaultPeriodStartDay < 1 || defaultPeriodStartDay > 28) {
      throw new BadRequestException("periodStartDay must be between 1 and 28");
    }

    var settings = UserSettings.builder()
        .baseCurrency(defaultCurrency)
        .periodStartDay(defaultPeriodStartDay)
        .build();
    return repo.save(settings);
  }

  @Transactional
  public UserSettings update(String baseCurrency, int periodStartDay) {
    if (periodStartDay < 1 || periodStartDay > 28) {
      throw new BadRequestException("periodStartDay must be between 1 and 28");
    }
    var s = getOrCreate();
    s.setBaseCurrency(baseCurrency);
    s.setPeriodStartDay(periodStartDay);
    return repo.save(s);
  }
}
