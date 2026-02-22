package com.cmm.mit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cmm.mit.exception.GlobalExceptionHandler;
import com.cmm.mit.service.PeriodService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PeriodController.class)
@Import(GlobalExceptionHandler.class)
class PeriodControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PeriodService periodService;

  @Test
  void current_returnsOk() throws Exception {
    when(periodService.currentPeriod(any()))
        .thenReturn(new PeriodService.Period(LocalDate.parse("2026-02-25"), LocalDate.parse("2026-03-24"), 25));

    mockMvc
        .perform(get("/api/period/current"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.periodStart").value("2026-02-25"))
        .andExpect(jsonPath("$.periodStartDay").value(25));
  }
}
