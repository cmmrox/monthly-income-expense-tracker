package com.cmm.mit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cmm.mit.dto.MeResponse;
import com.cmm.mit.dto.PatchSettingsRequest;
import com.cmm.mit.exception.GlobalExceptionHandler;
import com.cmm.mit.mapper.SettingsMapper;
import com.cmm.mit.service.SettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MeController.class)
@Import(GlobalExceptionHandler.class)
class MeControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private SettingsService settingsService;
  @MockBean private SettingsMapper settingsMapper;

  @Test
  void me_returnsOk() throws Exception {
    when(settingsService.getOrCreate()).thenReturn(null);
    when(settingsMapper.toMeResponse(null)).thenReturn(sampleMe());

    mockMvc.perform(get("/api/me")).andExpect(status().isOk()).andExpect(jsonPath("$.baseCurrency").value("USD"));
  }

  @Test
  void patch_whenValid_returnsOk() throws Exception {
    when(settingsService.update("USD", 10)).thenReturn(null);
    when(settingsMapper.toMeResponse(null)).thenReturn(sampleMe());

    var req = new PatchSettingsRequest("USD", 10);

    mockMvc
        .perform(
            patch("/api/me/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.periodStartDay").value(10));
  }

  @Test
  void patch_whenInvalid_triggersValidation() throws Exception {
    var invalidJson = "{\"baseCurrency\":\"US\",\"periodStartDay\":0}";

    mockMvc
        .perform(patch("/api/me/settings").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  private static MeResponse sampleMe() {
    return new MeResponse(
        UUID.fromString("11111111-1111-1111-1111-111111111111"),
        "USD",
        10,
        Instant.parse("2026-01-01T00:00:00Z"),
        Instant.parse("2026-01-01T00:00:00Z"));
  }
}
