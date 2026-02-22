package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.dto.CategoryDtos;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  CategoryDtos.CategoryResponse toResponse(Category category);

  CategoryDtos.CategoryRef toRef(Category category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Category toEntity(CategoryDtos.CreateCategoryRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(CategoryDtos.UpdateCategoryRequest request, @MappingTarget Category category);
}
