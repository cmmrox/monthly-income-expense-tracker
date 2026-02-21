package com.cmm.mit.repo;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.CategoryType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, UUID> {
  List<Category> findAllByActiveTrueOrderByNameAsc();
  List<Category> findAllByTypeAndActiveTrueOrderByNameAsc(CategoryType type);
}
