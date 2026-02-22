package com.cmm.mit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cmm.mit.domain.enums.TransactionType;
import com.cmm.mit.dto.CreateTransferRequest;
import com.cmm.mit.dto.CreateTxnRequest;
import com.cmm.mit.dto.PageResponse;
import com.cmm.mit.dto.TxnResponse;
import com.cmm.mit.exception.GlobalExceptionHandler;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.service.TxnService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private TxnService service;

  @Test
  void create_returnsOk() throws Exception {
    var id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    when(service.create(any(CreateTxnRequest.class))).thenReturn(sampleTxn(id, TransactionType.EXPENSE));

    var request =
        new CreateTxnRequest(
            Instant.parse("2026-02-01T00:00:00Z"),
            TransactionType.EXPENSE,
            new BigDecimal("10.00"),
            null,
            null,
            "desc",
            null,
            null);

    mockMvc
        .perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.type").value("EXPENSE"));
  }

  @Test
  void create_whenInvalid_triggersValidation() throws Exception {
    // missing required txnDate/type/amount
    var invalidJson = "{}";

    mockMvc
        .perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void transfer_returnsOk() throws Exception {
    var id = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(service.transfer(any(CreateTransferRequest.class))).thenReturn(sampleTxn(id, TransactionType.TRANSFER));

    var request =
        new CreateTransferRequest(
            Instant.parse("2026-02-01T00:00:00Z"),
            new BigDecimal("1.00"),
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            "x");

    mockMvc
        .perform(
            post("/api/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.type").value("TRANSFER"));
  }

  @Test
  void list_returnsOk() throws Exception {
    when(service.search(any(), any(), any(), any(), any(), any()))
        .thenReturn(new PageResponse<>(List.of(sampleTxn(UUID.randomUUID(), TransactionType.INCOME)), 0, 20, 1, 1));

    mockMvc
        .perform(
            get("/api/transactions")
                .param("from", "2026-01-01T00:00:00Z")
                .param("to", "2026-02-01T00:00:00Z"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.page").value(0));
  }

  @Test
  void get_returnsOk() throws Exception {
    var id = UUID.fromString("33333333-3333-3333-3333-333333333333");
    when(service.get(eq(id))).thenReturn(sampleTxn(id, TransactionType.INCOME));

    mockMvc.perform(get("/api/transactions/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id.toString()));
  }

  @Test
  void get_whenNotFound_returns404ProblemDetail() throws Exception {
    var id = UUID.fromString("44444444-4444-4444-4444-444444444444");
    when(service.get(eq(id))).thenThrow(new NotFoundException("Txn not found"));

    mockMvc
        .perform(get("/api/transactions/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
  }

  @Test
  void delete_returnsOk() throws Exception {
    var id = UUID.fromString("55555555-5555-5555-5555-555555555555");
    doNothing().when(service).delete(id);

    mockMvc.perform(delete("/api/transactions/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
  }

  private static TxnResponse sampleTxn(UUID id, TransactionType type) {
    return new TxnResponse(
        id,
        Instant.parse("2026-02-01T00:00:00Z"),
        type,
        new BigDecimal("1.00"),
        null,
        null,
        null,
        null,
        "desc",
        "m",
        "card",
        Instant.parse("2026-02-01T00:00:00Z"),
        Instant.parse("2026-02-01T00:00:00Z"));
  }
}
