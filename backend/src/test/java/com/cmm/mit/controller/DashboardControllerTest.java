package com.cmm.mit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cmm.mit.domain.enums.AccountType;
import com.cmm.mit.dto.AccountRef;
import com.cmm.mit.dto.AccountRollup;
import com.cmm.mit.dto.SummaryResponse;
import com.cmm.mit.exception.GlobalExceptionHandler;
import com.cmm.mit.service.DashboardService;
import com.cmm.mit.service.PeriodService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DashboardController.class)
@Import(GlobalExceptionHandler.class)
class DashboardControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PeriodService periodService;
  @MockBean private DashboardService dashboardService;

  @Test
  void summary_whenNoParams_usesCurrentPeriod() throws Exception {
    when(periodService.currentPeriod(any(Clock.class)))
        .thenReturn(new PeriodService.Period(LocalDate.parse("2026-02-25"), LocalDate.parse("2026-03-24"), 25));

    when(dashboardService.summary(eq(LocalDate.parse("2026-02-25")), eq(LocalDate.parse("2026-03-24"))))
        .thenReturn(sampleSummary(LocalDate.parse("2026-02-25"), LocalDate.parse("2026-03-24")));

    mockMvc
        .perform(get("/api/dashboard/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.from").value("2026-02-25"))
        .andExpect(jsonPath("$.incomeTotal").value(100));
  }

  @Test
  void summary_whenParamsProvided_usesProvidedRange() throws Exception {
    when(periodService.currentPeriod(any(Clock.class)))
        .thenReturn(new PeriodService.Period(LocalDate.parse("2026-02-25"), LocalDate.parse("2026-03-24"), 25));

    when(dashboardService.summary(eq(LocalDate.parse("2026-01-01")), eq(LocalDate.parse("2026-01-31"))))
        .thenReturn(sampleSummary(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-01-31")));

    mockMvc
        .perform(get("/api/dashboard/summary").param("from", "2026-01-01").param("to", "2026-01-31"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.from").value("2026-01-01"))
        .andExpect(jsonPath("$.to").value("2026-01-31"));
  }

  @Test
  void recentExpenses_returnsOk() throws Exception {
    when(dashboardService.recentExpenses(10)).thenReturn(List.of());

    mockMvc.perform(get("/api/dashboard/recent-expenses")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
  }

  @Test
  void byCategory_returnsOk() throws Exception {
    when(dashboardService.expenseByCategory(eq(LocalDate.parse("2026-01-01")), eq(LocalDate.parse("2026-01-31"))))
        .thenReturn(new com.cmm.mit.dto.ByCategoryResponse(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-01-31"), List.of()));

    mockMvc
        .perform(get("/api/reports/expenses/by-category").param("from", "2026-01-01").param("to", "2026-01-31"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray());
  }

  private static SummaryResponse sampleSummary(LocalDate from, LocalDate to) {
    return new SummaryResponse(
        from,
        to,
        new BigDecimal("100.00"),
        new BigDecimal("40.00"),
        new BigDecimal("60.00"),
        List.of(
            new AccountRollup(
                new AccountRef(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Cash", AccountType.CASH),
                new BigDecimal("100.00"),
                new BigDecimal("40.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO)));
  }
}
