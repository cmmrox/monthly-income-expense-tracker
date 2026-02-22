package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.CategoryResponse;
import com.cmm.mit.dto.CreateCategoryRequest;
import com.cmm.mit.dto.UpdateCategoryRequest;
import java.util.List;
import java.util.UUID;

/**
 * Category service API.
 *
 * <p>Contains category-related business rules/orchestration. Controllers must remain thin.
 */
public interface CategoryService {

  /**
   * List active categories, optionally filtered by type.
   */
  List<CategoryResponse> list(CategoryType type);

  /**
   * Create a new category.
   */
  CategoryResponse create(CreateCategoryRequest request);

  /**
   * Update an existing category.
   *
   * @throws com.cmm.mit.exception.NotFoundException when the category does not exist
   */
  CategoryResponse update(UUID categoryId, UpdateCategoryRequest request);

  /**
   * Soft-delete (deactivate) a category.
   *
   * @throws com.cmm.mit.exception.NotFoundException when the category does not exist
   */
  void delete(UUID categoryId);

  /**
   * Fetch a category entity for internal orchestration (e.g., transaction creation).
   *
   * <p>Avoid using from controllers.
   *
   * @throws com.cmm.mit.exception.NotFoundException when the category does not exist
   */
  Category getEntity(UUID categoryId);
}
