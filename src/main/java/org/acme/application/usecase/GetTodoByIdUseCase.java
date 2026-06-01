package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.models.Todo;
import org.acme.domain.repository.TodoRepository;

import java.util.UUID;

@ApplicationScoped
public class GetTodoByIdUseCase {
    private final TodoRepository todoRepository;

    @Inject
    public GetTodoByIdUseCase(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }

    public Todo execute(UUID id){
        return todoRepository.findTodoById(id)
                .orElseThrow(() -> new NotFoundException("Todo not found with id: " + id));
    }
}
