package com.cmm.mit.controller;

import com.cmm.mit.dto.PeriodResponse;
import com.cmm.mit.service.PeriodService;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/period")
@RequiredArgsConstructor
public class PeriodController {

  private final PeriodService periodService;

  @GetMapping("/current")
  public ResponseEntity<PeriodResponse> current() {
    var p = periodService.currentPeriod(Clock.systemUTC());
    return ResponseEntity.ok(new PeriodResponse(p.start(), p.end(), p.startDay()));
  }
}
