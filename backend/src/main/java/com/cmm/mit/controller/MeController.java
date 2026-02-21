package com.cmm.mit.controller;

import com.cmm.mit.dto.ApiEnvelope;
import com.cmm.mit.dto.MeDtos;
import com.cmm.mit.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

  private final SettingsService settingsService;

  @GetMapping
  public ApiEnvelope<MeDtos.MeResponse> me() {
    var s = settingsService.getOrCreate();
    return ApiEnvelope.ok(new MeDtos.MeResponse(s.getId(), s.getBaseCurrency(), s.getPeriodStartDay(), s.getCreatedAt(), s.getUpdatedAt()));
  }

  @PatchMapping("/settings")
  public ApiEnvelope<MeDtos.MeResponse> patch(@Valid @RequestBody MeDtos.PatchSettingsRequest req) {
    var s = settingsService.update(req.baseCurrency(), req.periodStartDay());
    return ApiEnvelope.ok(new MeDtos.MeResponse(s.getId(), s.getBaseCurrency(), s.getPeriodStartDay(), s.getCreatedAt(), s.getUpdatedAt()));
  }
}
