package org.acme.domain.repository;

import org.acme.domain.models.Todo;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findTodoById(UUID id);
    List<Todo> getAll();
    List<Todo> findAllByOwner(UUID ownerId);
    List<Todo> searchByOwner(UUID ownerId, String query);
    List<Todo> findTasksByParent(UUID parentId);
    boolean deleteById(UUID id);
    Optional<Todo> update(UUID id, Todo changes);
}
