package com.cmm.mit.controller;

import com.cmm.mit.dto.PeriodResponse;
import com.cmm.mit.service.PeriodService;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Period HTTP API.
 *
 * <p>Exposes salary-cycle period calculations.
 */
@RestController
@RequestMapping("/api/period")
@RequiredArgsConstructor
public class PeriodController {

  private final PeriodService periodService;

  /**
   * Get the current salary-cycle period.
   */
  @GetMapping("/current")
  public ResponseEntity<PeriodResponse> current() {
    var p = periodService.currentPeriod(Clock.systemUTC());
    return ResponseEntity.ok(new PeriodResponse(p.start(), p.end(), p.startDay()));
  }
}
