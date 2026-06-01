package org.acme.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.domain.models.Todo;
import org.acme.domain.models.demo.TodoView;
import org.acme.domain.repository.TodoDemoRepository;
import org.acme.infrastructure.entities.TodoEntity;
import org.acme.infrastructure.mapper.TodoMapper;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TodoDemoRepositoryImpl implements TodoDemoRepository {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public List<Todo> lazyOk() {
        List<TodoEntity> todos = em.createQuery(
                "SELECT t FROM TodoEntity t", TodoEntity.class
        ).getResultList();
        return todos.stream().map(TodoMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Todo> eagerSimulation() {
        List<TodoEntity> todos = em.createQuery(
                "SELECT t FROM TodoEntity t", TodoEntity.class
        ).getResultList();
        for (TodoEntity t : todos) {
            t.getOwner().getEmail();
            t.getCategories().size();
            t.getComments().size();
        }
        return todos.stream().map(TodoMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Todo> nPlusOne() {
        List<TodoEntity> todos = em.createQuery(
                "SELECT t FROM TodoEntity t", TodoEntity.class
        ).getResultList();
        for (TodoEntity t : todos) {
            t.getComments().size();
        }
        return todos.stream().map(TodoMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Todo> joinFetch() {
        List<TodoEntity> todos = em.createQuery(
                "SELECT DISTINCT t FROM TodoEntity t " +
                        "LEFT JOIN FETCH t.owner " +
                        "LEFT JOIN FETCH t.categories "+
                        "LEFT JOIN FETCH t.comments",
                TodoEntity.class
        ).getResultList();
        return todos.stream().map(TodoMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Todo> entityGraph() {
        EntityGraph<?> graph = em.getEntityGraph("Todo.justWithOwner");
        List<TodoEntity> todos = em.createQuery(
                "SELECT t FROM TodoEntity t", TodoEntity.class
        )
                .setHint("jakarta.persistence.loadgraph", graph)
                .getResultList();
        return todos.stream().map(TodoMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TodoView> projection() {
        return em.createQuery(
                "SELECT new org.acme.domain.models.demo.TodoView(t.id, t.title, o.email) " +
                        "FROM TodoEntity t JOIN t.owner o",
                TodoView.class
        ).getResultList();
    }

    @Override
    @Transactional
    public String lazyInitException() {
        List<TodoEntity> todos = em.createQuery(
                "SELECT t FROM TodoEntity t", TodoEntity.class
        ).getResultList();
        for (TodoEntity t : todos) {
            em.detach(t);
        }
        try {
            int total = 0;
            for (TodoEntity t : todos) {
                total += t.getComments().size();
            }
            return "Sin excepcion (inesperado). Total comments: " + total;
        } catch (Exception e) {
            return "LazyInitializationException: " + e.getMessage();
        }
    }
}
