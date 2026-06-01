package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.domain.models.Category;
import org.acme.domain.repository.CategoryRepository;

import java.util.List;

@ApplicationScoped
public class GetAllCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public GetAllCategoriesUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> execute() {
        return categoryRepository.getAll();
    }
}
