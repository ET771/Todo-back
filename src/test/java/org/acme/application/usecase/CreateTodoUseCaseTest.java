package org.acme.application.usecase;

import org.acme.application.dto.CreateTodoDto;
import org.acme.domain.models.Todo;
import org.acme.domain.models.User;
import org.acme.domain.repository.TodoRepository;
import org.acme.infrastructure.messaging.rabbitmq.TodoCreatedProducer;
import org.acme.infrastructure.security.AuthContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateTodoUseCaseTest {

    private TodoRepository todoRepository;
    private AuthContext authContext;
    private TodoCreatedProducer todoCreatedProducer;
    private CreateTodoUseCase useCase;

    @BeforeEach
    void setUp() {
        todoRepository = mock(TodoRepository.class);
        authContext = mock(AuthContext.class);
        todoCreatedProducer = mock(TodoCreatedProducer.class);

        User authenticatedUser = new User(
                UUID.randomUUID(),
                "test@test.com",
                "Test User",
                "USER",
                true,
                "firebase-uid"
        );
        when(authContext.getUser()).thenReturn(authenticatedUser);
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase = new CreateTodoUseCase(todoRepository, authContext, todoCreatedProducer);
    }

    @Test
    void executeShouldPersistTodoWithGeneratedIdAndCreatedAt() {
        CreateTodoDto dto = new CreateTodoDto();
        dto.setTitle("Buy milk");
        dto.setDescription("2 liters of whole milk");

        Todo result = useCase.execute(dto);

        assertNotNull(result);
        assertEquals("Buy milk", result.getTitle());
        assertEquals("2 liters of whole milk", result.getDescription());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
    }
}
