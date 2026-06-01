package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.domain.models.Todo;
import org.acme.domain.repository.TodoRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GetTasksByListUseCase {

    private final TodoRepository todoRepository;

    @Inject
    public GetTasksByListUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> execute(UUID listId) {
        return todoRepository.findTasksByParent(listId);
    }
}
