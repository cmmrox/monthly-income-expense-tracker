package com.cmm.mit.controller;

import com.cmm.mit.dto.ApiEnvelope;
import com.cmm.mit.dto.DashboardDtos;
import com.cmm.mit.service.PeriodService;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/period")
@RequiredArgsConstructor
public class PeriodController {

  private final PeriodService periodService;

  @GetMapping("/current")
  public ApiEnvelope<DashboardDtos.PeriodResponse> current() {
    var p = periodService.currentPeriod(Clock.systemUTC());
    return ApiEnvelope.ok(new DashboardDtos.PeriodResponse(p.start(), p.end(), p.startDay()));
  }
}
