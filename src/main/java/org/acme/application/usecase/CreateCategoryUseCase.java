package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.dto.CreateCategoryDto;
import org.acme.domain.models.Category;
import org.acme.domain.repository.CategoryRepository;

import java.util.UUID;

@ApplicationScoped
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public CreateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category execute(CreateCategoryDto dto) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setIcon(dto.getIcon());
        return categoryRepository.save(category);
    }
}
