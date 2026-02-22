package com.cmm.mit.service;

import com.cmm.mit.domain.entity.UserSettings;

public interface SettingsService {

  UserSettings getOrCreate();

  UserSettings update(String baseCurrency, int periodStartDay);
}
