package org.acme.infrastructure.repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.domain.models.Todo;
import org.acme.domain.models.User;
import org.acme.domain.repository.TodoRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class TodoRepositoryImplTest {

    // Seed user inserted by import.sql
    private static final UUID SEED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Inject
    TodoRepository todoRepository;

    @Test
    void saveShouldPersistTodo() {
        User owner = new User(SEED_USER_ID, "ada@example.com", "Ada Lovelace", "admin", true, "seed-firebase-1");

        Todo todo = new Todo();
        todo.setId(UUID.randomUUID());
        todo.setTitle("Title H2");
        todo.setDescription("Description H2");
        todo.setCreatedAt(LocalDateTime.now());
        todo.setOwner(owner);

        Todo saved = todoRepository.save(todo);

        assertNotNull(saved);
        assertEquals(todo.getId(), saved.getId());
        assertEquals("Title H2", saved.getTitle());
        assertEquals("Description H2", saved.getDescription());
        assertNotNull(saved.getCreatedAt());
    }
}
