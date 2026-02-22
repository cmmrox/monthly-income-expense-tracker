package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.CategoryDtos;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

  List<CategoryDtos.CategoryResponse> list(CategoryType type);

  CategoryDtos.CategoryResponse create(CategoryDtos.CreateCategoryRequest request);

  CategoryDtos.CategoryResponse update(UUID categoryId, CategoryDtos.UpdateCategoryRequest request);

  void delete(UUID categoryId);

  /** Internal use (e.g., Txn creation). Avoid using from controllers. */
  Category getEntity(UUID categoryId);
}
