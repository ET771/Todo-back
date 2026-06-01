package org.acme.domain.repository;

import org.acme.domain.models.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findCategoryById(UUID id);
    List<Category> getAll();
    boolean deleteById(UUID id);
}
