package com.cmm.mit.controller;

import com.cmm.mit.dto.ApiEnvelope;
import com.cmm.mit.dto.MeDtos;
import com.cmm.mit.mapper.SettingsMapper;
import com.cmm.mit.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

  private final SettingsService settingsService;
  private final SettingsMapper settingsMapper;

  @GetMapping
  public ApiEnvelope<MeDtos.MeResponse> me() {
    return ApiEnvelope.ok(settingsMapper.toMeResponse(settingsService.getOrCreate()));
  }

  @PatchMapping("/settings")
  public ApiEnvelope<MeDtos.MeResponse> patch(@Valid @RequestBody MeDtos.PatchSettingsRequest request) {
    return ApiEnvelope.ok(settingsMapper.toMeResponse(settingsService.update(request.baseCurrency(), request.periodStartDay())));
  }
}
