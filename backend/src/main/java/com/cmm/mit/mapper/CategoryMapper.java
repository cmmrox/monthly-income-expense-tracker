package com.cmm.mit.mapper;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.dto.CategoryRef;
import com.cmm.mit.dto.CategoryResponse;
import com.cmm.mit.dto.CreateCategoryRequest;
import com.cmm.mit.dto.UpdateCategoryRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  CategoryResponse toResponse(Category category);

  CategoryRef toRef(Category category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Category toEntity(CreateCategoryRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(UpdateCategoryRequest request, @MappingTarget Category category);
}
