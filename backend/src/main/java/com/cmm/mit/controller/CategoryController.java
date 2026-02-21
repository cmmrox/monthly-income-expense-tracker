package com.cmm.mit.controller;

import com.cmm.mit.domain.entity.Category;
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
    return ApiEnvelope.ok(service.list(type).stream().map(CategoryController::toResponse).toList());
  }

  @PostMapping
  public ApiEnvelope<CategoryDtos.CategoryResponse> create(@Valid @RequestBody CategoryDtos.CreateCategoryRequest req) {
    var c = Category.builder()
        .name(req.name())
        .type(req.type())
        .color(req.color())
        .icon(req.icon())
        .build();
    return ApiEnvelope.ok(toResponse(service.create(c)));
  }

  @PutMapping("/{id}")
  public ApiEnvelope<CategoryDtos.CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CategoryDtos.UpdateCategoryRequest req) {
    var patch = Category.builder()
        .name(req.name())
        .type(req.type())
        .color(req.color())
        .icon(req.icon())
        .active(req.active())
        .build();
    return ApiEnvelope.ok(toResponse(service.update(id, patch)));
  }

  @DeleteMapping("/{id}")
  public ApiEnvelope<java.util.Map<String, Object>> delete(@PathVariable UUID id) {
    service.delete(id);
    return ApiEnvelope.ok(java.util.Map.of("ok", true));
  }

  static CategoryDtos.CategoryResponse toResponse(Category c) {
    return new CategoryDtos.CategoryResponse(c.getId(), c.getName(), c.getType(), c.getColor(), c.getIcon(), c.isActive(), c.getCreatedAt(), c.getUpdatedAt());
  }

  public static CategoryDtos.CategoryRef toRef(Category c) {
    if (c == null) return null;
    return new CategoryDtos.CategoryRef(c.getId(), c.getName(), c.getType(), c.getColor());
  }
}
