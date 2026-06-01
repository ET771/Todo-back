package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.repository.CategoryRepository;

import java.util.UUID;

@ApplicationScoped
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void execute(UUID id) {
        boolean deleted = categoryRepository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Category not found: " + id);
        }
    }
}
