package com.cmm.mit.service.impl;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.CategoryResponse;
import com.cmm.mit.dto.CreateCategoryRequest;
import com.cmm.mit.dto.UpdateCategoryRequest;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.mapper.CategoryMapper;
import com.cmm.mit.repo.CategoryRepo;
import com.cmm.mit.service.CategoryService;
import com.cmm.mit.util.LogSanitizer;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepo repo;
  private final CategoryMapper mapper;

  @Override
  public List<CategoryResponse> list(CategoryType type) {
    log.info("CategoryService.list(type={}) start", type);

    var entities = (type == null)
        ? repo.findAllByActiveTrueOrderByNameAsc()
        : repo.findAllByTypeAndActiveTrueOrderByNameAsc(type);

    List<CategoryResponse> result = entities.stream().map(mapper::toResponse).toList();
    log.info("CategoryService.list(...) end: count={}", result.size());
    return result;
  }

  @Override
  @Transactional
  public CategoryResponse create(CreateCategoryRequest request) {
    log.info("CategoryService.create(request={}) start", LogSanitizer.safe(request));

    Category category = mapper.toEntity(request);
    category.setActive(true);

    Category saved = repo.save(category);
    CategoryResponse response = mapper.toResponse(saved);

    log.info("CategoryService.create(...) end: categoryId={}", response.id());
    return response;
  }

  @Override
  @Transactional
  public CategoryResponse update(UUID categoryId, UpdateCategoryRequest request) {
    log.info("CategoryService.update(categoryId={}, request={}) start", categoryId, LogSanitizer.safe(request));

    Category category = getEntity(categoryId);
    mapper.updateEntity(request, category);

    Category saved = repo.save(category);
    CategoryResponse response = mapper.toResponse(saved);

    log.info("CategoryService.update(...) end: categoryId={}", response.id());
    return response;
  }

  @Override
  @Transactional
  public void delete(UUID categoryId) {
    log.info("CategoryService.delete(categoryId={}) start", categoryId);

    Category category = getEntity(categoryId);
    category.setActive(false);
    repo.save(category);

    log.info("CategoryService.delete(...) end: categoryId={}", categoryId);
  }

  @Override
  public Category getEntity(UUID categoryId) {
    log.info("CategoryService.getEntity(categoryId={}) start", categoryId);

    Category category = repo.findById(categoryId)
        .orElseThrow(() -> new NotFoundException("Category not found"));

    log.info("CategoryService.getEntity(...) end: categoryId={}", category.getId());
    return category;
  }
}
