package org.acme.application.usecase;

import org.acme.application.dto.UpdateTodoDto;
import org.acme.domain.models.Priority;
import org.acme.domain.models.Todo;
import org.acme.domain.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.NotFoundException;

class UpdateTodoUseCaseTest {

    private TodoRepository todoRepository;
    private UpdateTodoUseCase useCase;

    @BeforeEach
    void setUp() {
        todoRepository = mock(TodoRepository.class);
        useCase = new UpdateTodoUseCase(todoRepository);
    }

    @Test
    void executeShouldReturnUpdatedTodo() {
        UUID id = UUID.randomUUID();
        Todo updated = new Todo();
        updated.setId(id);
        updated.setTitle("Updated Title");
        updated.setDescription("Updated Desc");
        updated.setPriority(Priority.HIGH);
        updated.setUpdatedAt(LocalDateTime.now());

        when(todoRepository.update(eq(id), any(Todo.class))).thenReturn(Optional.of(updated));

        UpdateTodoDto dto = new UpdateTodoDto();
        dto.setTitle("Updated Title");
        dto.setDescription("Updated Desc");
        dto.setPriority(Priority.HIGH);

        Todo result = useCase.execute(id, dto);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals(Priority.HIGH, result.getPriority());
    }

    @Test
    void executeShouldThrowNotFoundWhenTodoDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(todoRepository.update(eq(id), any(Todo.class))).thenReturn(Optional.empty());

        UpdateTodoDto dto = new UpdateTodoDto();
        dto.setTitle("Irrelevant");

        assertThrows(NotFoundException.class, () -> useCase.execute(id, dto));
    }
}
