package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.CategoryResponse;
import com.cmm.mit.dto.CreateCategoryRequest;
import com.cmm.mit.dto.UpdateCategoryRequest;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

  List<CategoryResponse> list(CategoryType type);

  CategoryResponse create(CreateCategoryRequest request);

  CategoryResponse update(UUID categoryId, UpdateCategoryRequest request);

  void delete(UUID categoryId);

  /** Internal use (e.g., Txn creation). Avoid using from controllers. */
  Category getEntity(UUID categoryId);
}
