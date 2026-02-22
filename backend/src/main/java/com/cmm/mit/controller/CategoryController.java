package com.cmm.mit.controller;

import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.CategoryResponse;
import com.cmm.mit.dto.CreateCategoryRequest;
import com.cmm.mit.dto.UpdateCategoryRequest;
import com.cmm.mit.service.CategoryService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Category HTTP API.
 *
 * <p>Thin controller: delegates category-related business logic to {@link com.cmm.mit.service.CategoryService}.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService service;

  /**
   * List active categories, optionally filtered by type.
   */
  @GetMapping
  public ResponseEntity<java.util.List<CategoryResponse>> list(@RequestParam(required = false) CategoryType type) {
    return ResponseEntity.ok(service.list(type));
  }

  /**
   * Create a category.
   */
  @PostMapping
  public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
    return ResponseEntity.ok(service.create(request));
  }

  /**
   * Update an existing category.
   */
  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateCategoryRequest request) {

    return ResponseEntity.ok(service.update(id, request));
  }

  /**
   * Soft-delete (deactivate) a category.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.ok(java.util.Map.of("ok", true));
  }
}
