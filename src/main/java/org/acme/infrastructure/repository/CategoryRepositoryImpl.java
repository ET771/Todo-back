package org.acme.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.domain.models.Category;
import org.acme.domain.repository.CategoryRepository;
import org.acme.infrastructure.entities.CategoryEntity;
import org.acme.infrastructure.mapper.CategoryMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryRepositoryImpl implements CategoryRepository, PanacheRepositoryBase<CategoryEntity, UUID> {

    @Override
    @Transactional
    public Category save(Category category) {
        CategoryEntity entity = CategoryMapper.toEntity(category);
        persist(entity);
        return CategoryMapper.toDomain(entity);
    }

    @Override
    public Optional<Category> findCategoryById(UUID id) {
        return findByIdOptional(id).map(CategoryMapper::toDomain);
    }

    @Override
    public List<Category> getAll() {
        return listAll()
                .stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteById(UUID id) {
        return findByIdOptional(id).map(entity -> {
            delete(entity);
            return true;
        }).orElse(false);
    }
}
