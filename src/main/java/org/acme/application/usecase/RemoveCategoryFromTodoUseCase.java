package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.infrastructure.entities.CategoryEntity;
import org.acme.infrastructure.entities.TodoEntity;
import org.acme.infrastructure.repository.CategoryRepositoryImpl;
import org.acme.infrastructure.repository.TodoRepositoryImpl;

import java.util.UUID;

@ApplicationScoped
public class RemoveCategoryFromTodoUseCase {

    private final TodoRepositoryImpl todoRepository;
    private final CategoryRepositoryImpl categoryRepository;

    @Inject
    public RemoveCategoryFromTodoUseCase(TodoRepositoryImpl todoRepository, CategoryRepositoryImpl categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void execute(UUID todoId, UUID categoryId) {
        TodoEntity todo = todoRepository.findByIdOptional(todoId)
                .orElseThrow(() -> new NotFoundException("Todo not found: " + todoId));

        CategoryEntity category = categoryRepository.findByIdOptional(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));

        todo.getCategories().remove(category);
    }
}
