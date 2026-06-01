package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.dto.CreateTodoDto;
import org.acme.domain.models.Todo;
import org.acme.domain.repository.TodoRepository;
import org.acme.infrastructure.messaging.TodoEvent;
import org.acme.infrastructure.messaging.rabbitmq.TodoCreatedProducer;
import org.acme.infrastructure.security.AuthContext;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class CreateTodoUseCase {

    private final TodoRepository todoRepository;
    private final AuthContext authContext;
    private final TodoCreatedProducer todoCreatedProducer;

    @Inject
    public CreateTodoUseCase(TodoRepository todoRepository, AuthContext authContext,
                             TodoCreatedProducer todoCreatedProducer) {
        this.todoRepository = todoRepository;
        this.authContext = authContext;
        this.todoCreatedProducer = todoCreatedProducer;
    }

    public Todo execute(CreateTodoDto todoDto) {
        Todo todo = new Todo();
        todo.setId(UUID.randomUUID());
        todo.setTitle(todoDto.getTitle());
        todo.setDescription(todoDto.getDescription() != null ? todoDto.getDescription() : "");
        todo.setCreatedAt(LocalDateTime.now());
        todo.setOwner(authContext.getUser());
        if (todoDto.getParentId() != null) todo.setParentId(todoDto.getParentId());
        if (todoDto.getPriority() != null) todo.setPriority(todoDto.getPriority());
        if (todoDto.getDueDate() != null) todo.setDueDate(todoDto.getDueDate());
        Todo saved = todoRepository.save(todo);
        todoCreatedProducer.publish(new TodoEvent(
                saved.getId().toString(),
                saved.getTitle(),
                authContext.getUser().getEmail()
        ));
        return saved;
    }
}
