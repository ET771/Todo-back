package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.domain.models.Todo;
import org.acme.domain.repository.TodoRepository;
import org.acme.infrastructure.security.AuthContext;

import java.util.List;

@ApplicationScoped
public class SearchTodosUseCase {

    private final TodoRepository todoRepository;
    private final AuthContext authContext;

    @Inject
    public SearchTodosUseCase(TodoRepository todoRepository, AuthContext authContext) {
        this.todoRepository = todoRepository;
        this.authContext = authContext;
    }

    public List<Todo> execute(String query) {
        return todoRepository.searchByOwner(authContext.getUser().getId(), query);
    }
}
