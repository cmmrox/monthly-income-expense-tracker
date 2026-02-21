package com.cmm.mit.service;

import com.cmm.mit.domain.entity.Category;
import com.cmm.mit.domain.enums.CategoryType;
import com.cmm.mit.exception.NotFoundException;
import com.cmm.mit.repo.CategoryRepo;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepo repo;

  public List<Category> list(CategoryType type) {
    if (type == null) return repo.findAllByActiveTrueOrderByNameAsc();
    return repo.findAllByTypeAndActiveTrueOrderByNameAsc(type);
  }

  @Transactional
  public Category create(Category c) {
    c.setActive(true);
    return repo.save(c);
  }

  public Category get(UUID id) {
    return repo.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
  }

  @Transactional
  public Category update(UUID id, Category patch) {
    var c = get(id);
    c.setName(patch.getName());
    c.setType(patch.getType());
    c.setColor(patch.getColor());
    c.setIcon(patch.getIcon());
    c.setActive(patch.isActive());
    return repo.save(c);
  }

  @Transactional
  public void delete(UUID id) {
    var c = get(id);
    c.setActive(false);
    repo.save(c);
  }
}
