package com.cmm.mit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.dto.CategoryResponse;
import com.cmm.mit.dto.CreateCategoryRequest;
import com.cmm.mit.dto.UpdateCategoryRequest;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.mapper.CategoryMapper;
import com.cmm.mit.repo.CategoryRepo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

  @Mock CategoryRepo repo;
  @Mock CategoryMapper mapper;

  @InjectMocks CategoryServiceImpl service;

  @Captor ArgumentCaptor<Category> categoryCaptor;

  @Test
  void list_whenTypeNull_shouldListAllActive() {
    var entity = Category.builder().id(UUID.randomUUID()).name("Salary").type(CategoryType.INCOME)
        .active(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();

    when(repo.findAllByActiveTrueOrderByNameAsc()).thenReturn(List.of(entity));
    when(mapper.toResponse(entity)).thenReturn(new CategoryResponse(entity.getId(), entity.getName(), entity.getType(), entity.getColor(), entity.getIcon(),
        entity.isActive(), entity.getCreatedAt(), entity.getUpdatedAt()));

    List<CategoryResponse> result = service.list(null);

    assertThat(result).hasSize(1);
    verify(repo).findAllByActiveTrueOrderByNameAsc();
  }

  @Test
  void create_shouldSetActiveTrueAndSave() {
    var request = new CreateCategoryRequest("Groceries", CategoryType.EXPENSE, "#fff", "cart");
    var mapped = Category.builder().name("Groceries").type(CategoryType.EXPENSE).active(false).build();

    when(mapper.toEntity(request)).thenReturn(mapped);
    when(repo.save(any(Category.class))).thenAnswer(inv -> {
      Category c = inv.getArgument(0);
      c.setId(UUID.randomUUID());
      c.setCreatedAt(Instant.now());
      c.setUpdatedAt(Instant.now());
      return c;
    });
    when(mapper.toResponse(any(Category.class))).thenAnswer(inv -> {
      Category c = inv.getArgument(0);
      return new CategoryResponse(c.getId(), c.getName(), c.getType(), c.getColor(), c.getIcon(), c.isActive(), c.getCreatedAt(), c.getUpdatedAt());
    });

    CategoryResponse response = service.create(request);

    verify(repo).save(categoryCaptor.capture());
    assertThat(categoryCaptor.getValue().isActive()).isTrue();
    assertThat(response.id()).isNotNull();
  }

  @Test
  void update_shouldApplyMapperUpdateAndSave() {
    UUID id = UUID.randomUUID();
    var existing = Category.builder().id(id).name("Old").type(CategoryType.EXPENSE).active(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
    var request = new UpdateCategoryRequest("New", CategoryType.INCOME, null, null, false);

    when(repo.findById(id)).thenReturn(Optional.of(existing));
    when(repo.save(existing)).thenReturn(existing);
    when(mapper.toResponse(existing)).thenReturn(new CategoryResponse(existing.getId(), existing.getName(), existing.getType(), existing.getColor(), existing.getIcon(),
        existing.isActive(), existing.getCreatedAt(), existing.getUpdatedAt()));

    CategoryResponse response = service.update(id, request);

    verify(mapper).updateEntity(request, existing);
    verify(repo).save(existing);
    assertThat(response.id()).isEqualTo(id);
  }

  @Test
  void delete_shouldSoftDeleteBySettingActiveFalse() {
    UUID id = UUID.randomUUID();
    var existing = Category.builder().id(id).active(true).build();

    when(repo.findById(id)).thenReturn(Optional.of(existing));
    when(repo.save(any(Category.class))).thenReturn(existing);

    service.delete(id);

    assertThat(existing.isActive()).isFalse();
    verify(repo).save(existing);
  }

  @Test
  void getEntity_whenNotFound_shouldThrow() {
    UUID id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getEntity(id))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Category not found");
  }
}
