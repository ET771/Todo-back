package org.acme.infrastructure.mapper;

import org.acme.domain.models.Category;
import org.acme.infrastructure.entities.CategoryEntity;

public class CategoryMapper {

    public static Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        Category category = new Category(entity.getId(), entity.getName());
        category.setDescription(entity.getDescription());
        category.setColor(entity.getColor());
        category.setIcon(entity.getIcon());
        return category;
    }

    public static CategoryEntity toEntity(Category category) {
        if (category == null) return null;
        CategoryEntity entity = new CategoryEntity();
        entity.setId(category.getId());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        entity.setColor(category.getColor());
        entity.setIcon(category.getIcon());
        return entity;
    }
}
