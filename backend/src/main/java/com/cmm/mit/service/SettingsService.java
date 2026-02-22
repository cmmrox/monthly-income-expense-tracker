package com.cmm.mit.service;

import com.cmm.mit.domain.entity.UserSettings;

/**
 * User settings service API.
 *
 * <p>Manages base currency and salary-cycle period configuration.
 */
public interface SettingsService {

  /**
   * Fetch the single settings row or create it using defaults.
   */
  UserSettings getOrCreate();

  /**
   * Update settings.
   *
   * @throws com.cmm.mit.exception.BadRequestException when periodStartDay is out of supported range
   */
  UserSettings update(String baseCurrency, int periodStartDay);
}
