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

import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.CategoryResponse;
import com.cmm.mit.dto.CreateCategoryRequest;
import com.cmm.mit.dto.UpdateCategoryRequest;
import com.cmm.mit.exception.GlobalExceptionHandler;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private CategoryService service;

  @Test
  void list_returnsOk() throws Exception {
    when(service.list(null))
        .thenReturn(
            List.of(
                new CategoryResponse(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "Groceries",
                    CategoryType.EXPENSE,
                    "#fff",
                    "cart",
                    true,
                    java.time.Instant.parse("2026-01-01T00:00:00Z"),
                    java.time.Instant.parse("2026-01-01T00:00:00Z"))));

    mockMvc.perform(get("/api/categories")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Groceries"));
  }

  @Test
  void create_whenValid_returnsOk() throws Exception {
    var id = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(service.create(any(CreateCategoryRequest.class)))
        .thenReturn(
            new CategoryResponse(
                id,
                "Salary",
                CategoryType.INCOME,
                null,
                null,
                true,
                java.time.Instant.parse("2026-01-01T00:00:00Z"),
                java.time.Instant.parse("2026-01-01T00:00:00Z")));

    var request = new CreateCategoryRequest("Salary", CategoryType.INCOME, null, null);

    mockMvc
        .perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));
  }

  @Test
  void create_whenInvalid_triggersValidation() throws Exception {
    var invalidJson = "{\"name\":\"\",\"type\":\"INCOME\"}";

    mockMvc
        .perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  void update_returnsOk() throws Exception {
    var id = UUID.fromString("33333333-3333-3333-3333-333333333333");
    when(service.update(eq(id), any(UpdateCategoryRequest.class)))
        .thenReturn(
            new CategoryResponse(
                id,
                "New",
                CategoryType.INCOME,
                null,
                null,
                false,
                java.time.Instant.parse("2026-01-01T00:00:00Z"),
                java.time.Instant.parse("2026-01-01T00:00:00Z")));

    var request = new UpdateCategoryRequest("New", CategoryType.INCOME, null, null, false);

    mockMvc
        .perform(
            put("/api/categories/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  void delete_returnsOk() throws Exception {
    var id = UUID.fromString("44444444-4444-4444-4444-444444444444");
    doNothing().when(service).delete(id);

    mockMvc
        .perform(delete("/api/categories/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(true));
  }

  @Test
  void update_whenServiceThrowsNotFound_returnsProblemDetail404() throws Exception {
    var id = UUID.fromString("55555555-5555-5555-5555-555555555555");
    when(service.update(eq(id), any(UpdateCategoryRequest.class))).thenThrow(new NotFoundException("Category not found"));

    var request = new UpdateCategoryRequest("New", CategoryType.INCOME, null, null, true);

    mockMvc
        .perform(
            put("/api/categories/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
  }
}
