package com.cmm.mit.controller;

import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.ApiEnvelope;
import com.cmm.mit.dto.CategoryDtos;
import com.cmm.mit.service.CategoryService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService service;

  @GetMapping
  public ApiEnvelope<java.util.List<CategoryDtos.CategoryResponse>> list(@RequestParam(required = false) CategoryType type) {
    return ApiEnvelope.ok(service.list(type));
  }

  @PostMapping
  public ApiEnvelope<CategoryDtos.CategoryResponse> create(@Valid @RequestBody CategoryDtos.CreateCategoryRequest request) {
    return ApiEnvelope.ok(service.create(request));
  }

  @PutMapping("/{id}")
  public ApiEnvelope<CategoryDtos.CategoryResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody CategoryDtos.UpdateCategoryRequest request) {

    return ApiEnvelope.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ApiEnvelope<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ApiEnvelope.ok(java.util.Map.of("ok", true));
  }
}
