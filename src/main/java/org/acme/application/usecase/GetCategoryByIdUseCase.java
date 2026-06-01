package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.models.Category;
import org.acme.domain.repository.CategoryRepository;

import java.util.UUID;

@ApplicationScoped
public class GetCategoryByIdUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public GetCategoryByIdUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category execute(UUID id) {
        return categoryRepository.findCategoryById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }
}
