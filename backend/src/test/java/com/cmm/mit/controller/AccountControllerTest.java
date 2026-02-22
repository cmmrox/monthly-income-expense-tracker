package com.cmm.mit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cmm.mit.domain.enums.AccountType;
import com.cmm.mit.dto.AccountResponse;
import com.cmm.mit.dto.CreateAccountRequest;
import com.cmm.mit.dto.UpdateAccountRequest;
import com.cmm.mit.exception.GlobalExceptionHandler;
import com.cmm.mit.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private AccountService service;

  @Test
  void list_returnsOkAndBody() throws Exception {
    when(service.listActive())
        .thenReturn(
            List.of(
                new AccountResponse(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "Cash",
                    AccountType.CASH,
                    "LKR",
                    new BigDecimal("10.00"),
                    true,
                    java.time.Instant.parse("2026-01-01T00:00:00Z"),
                    java.time.Instant.parse("2026-01-01T00:00:00Z"))));

    mockMvc
        .perform(get("/api/accounts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Cash"));
  }

  @Test
  void create_whenValid_returnsOk() throws Exception {
    var id = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(service.create(any(CreateAccountRequest.class)))
        .thenReturn(
            new AccountResponse(
                id,
                "Bank",
                AccountType.BANK,
                "USD",
                new BigDecimal("1.00"),
                true,
                java.time.Instant.parse("2026-01-01T00:00:00Z"),
                java.time.Instant.parse("2026-01-01T00:00:00Z")));

    var request = new CreateAccountRequest("Bank", AccountType.BANK, "USD", new BigDecimal("1.00"));

    mockMvc
        .perform(
            post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Bank"));
  }

  @Test
  void create_whenInvalid_triggersValidationProblemDetail() throws Exception {
    // name is @NotBlank in DTO, so empty string should fail.
    var invalidJson =
        "{\"name\":\"\",\"type\":\"BANK\",\"currency\":\"USD\",\"openingBalance\":1}";

    mockMvc
        .perform(post("/api/accounts").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation failed"))
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.errors").isArray());
  }

  @Test
  void update_returnsOk() throws Exception {
    var id = UUID.fromString("33333333-3333-3333-3333-333333333333");
    when(service.update(eq(id), any(UpdateAccountRequest.class)))
        .thenReturn(
            new AccountResponse(
                id,
                "New",
                AccountType.CASH,
                "LKR",
                new BigDecimal("0"),
                false,
                java.time.Instant.parse("2026-01-01T00:00:00Z"),
                java.time.Instant.parse("2026-01-01T00:00:00Z")));

    var request = new UpdateAccountRequest("New", AccountType.CASH, "LKR", new BigDecimal("0"), false);

    mockMvc
        .perform(
            put("/api/accounts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  void update_whenServiceThrowsBadRequest_returnsProblemDetail400() throws Exception {
    var id = UUID.fromString("99999999-9999-9999-9999-999999999999");
    when(service.update(eq(id), any(UpdateAccountRequest.class)))
        .thenThrow(new com.cmm.mit.exception.BadRequestException("Invalid"));

    var request = new UpdateAccountRequest("New", AccountType.CASH, "LKR", new BigDecimal("0"), false);

    mockMvc
        .perform(
            put("/api/accounts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
  }

  @Test
  void delete_returnsOk() throws Exception {
    var id = UUID.fromString("44444444-4444-4444-4444-444444444444");
    doNothing().when(service).delete(id);

    mockMvc
        .perform(delete("/api/accounts/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(true));
  }

  @Test
  void list_whenUnexpectedException_returns500ProblemDetail() throws Exception {
    when(service.listActive()).thenThrow(new RuntimeException("boom"));

    mockMvc
        .perform(get("/api/accounts"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errorCode").value("INTERNAL_ERROR"));
  }
}
