package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.application.dto.UpdateTodoDto;
import org.acme.domain.models.Todo;
import org.acme.domain.repository.TodoRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class UpdateTodoUseCase {

    private final TodoRepository todoRepository;

    @Inject
    public UpdateTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo execute(UUID id, UpdateTodoDto dto) {
        Todo changes = new Todo();
        changes.setTitle(dto.getTitle());
        changes.setDescription(dto.getDescription());
        changes.setDueDate(dto.getDueDate());
        changes.setPriority(dto.getPriority());
        changes.setUpdatedAt(LocalDateTime.now());
        if (dto.getCompleted() != null) {
            changes.setCompleted(dto.getCompleted());
        }

        return todoRepository.update(id, changes)
                .orElseThrow(() -> new NotFoundException("Todo not found: " + id));
    }
}
