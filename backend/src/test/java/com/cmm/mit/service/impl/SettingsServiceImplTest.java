package com.cmm.mit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.cmm.mit.domain.entity.UserSettings;
import com.cmm.mit.exception.BadRequestException;
import com.cmm.mit.repo.UserSettingsRepo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SettingsServiceImplTest {

  @Mock UserSettingsRepo repo;

  @InjectMocks SettingsServiceImpl service;

  @Test
  void getOrCreate_whenExisting_shouldReturnExisting() {
    var existing = UserSettings.builder().baseCurrency("LKR").periodStartDay(25).build();
    when(repo.findAll()).thenReturn(List.of(existing));

    var result = service.getOrCreate();

    assertThat(result).isSameAs(existing);
    verify(repo, never()).save(any());
  }

  @Test
  void getOrCreate_whenEmpty_shouldCreateWithDefaults() {
    when(repo.findAll()).thenReturn(List.of());

    service.defaultCurrency = "USD";
    service.defaultPeriodStartDay = 10;

    when(repo.save(any(UserSettings.class))).thenAnswer(inv -> inv.getArgument(0));

    var result = service.getOrCreate();

    assertThat(result.getBaseCurrency()).isEqualTo("USD");
    assertThat(result.getPeriodStartDay()).isEqualTo(10);
  }

  @Test
  void update_whenInvalidStartDay_shouldThrow() {
    assertThatThrownBy(() -> service.update("USD", 0))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("between 1 and 28");
  }
}
