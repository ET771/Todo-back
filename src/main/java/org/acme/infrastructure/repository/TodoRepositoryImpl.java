package org.acme.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.acme.domain.models.Todo;
import org.acme.domain.repository.TodoRepository;
import org.acme.infrastructure.entities.TodoEntity;
import org.acme.infrastructure.entities.UserEntity;
import org.acme.infrastructure.mapper.TodoMapper;

import java.util.Optional;
import java.util.UUID;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;


@ApplicationScoped
public class TodoRepositoryImpl implements TodoRepository, PanacheRepositoryBase<TodoEntity, UUID> {

    @Override
    @Transactional
    public Todo save(Todo todo) {
        TodoEntity entity = TodoMapper.toEntity(todo);
        if (todo.getOwner() != null && todo.getOwner().getId() != null) {
            entity.setOwner(getEntityManager().getReference(UserEntity.class, todo.getOwner().getId()));
        }
        if (todo.getParentId() != null) {
            entity.setParent(getEntityManager().getReference(TodoEntity.class, todo.getParentId()));
        }
        persist(entity);
        return TodoMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Optional<Todo> findTodoById(UUID id) {
        return findByIdOptional(id).map(entity -> {
            Hibernate.initialize(entity.getCategories());
            Hibernate.initialize(entity.getComments());
            Hibernate.initialize(entity.getTasks());
            return TodoMapper.toDomain(entity);
        });
    }

    @Override
    public List<Todo> getAll() {
        return listAll()
                .stream()
                .map(TodoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Todo> findAllByOwner(UUID ownerId) {
        List<TodoEntity> entities = find("owner.id = ?1 and parent is null", ownerId).list();
        for (TodoEntity entity : entities) {
            Hibernate.initialize(entity.getTasks());
        }
        return entities.stream()
                .map(TodoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Todo> searchByOwner(UUID ownerId, String query) {
        String likeQuery = "%" + query.toLowerCase() + "%";
        List<TodoEntity> entities = find(
            "owner.id = ?1 and (lower(title) like ?2 or lower(description) like ?2)",
            ownerId, likeQuery
        ).list();
        for (TodoEntity entity : entities) {
            Hibernate.initialize(entity.getCategories());
        }
        return entities.stream()
                .map(TodoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Todo> findTasksByParent(UUID parentId) {
        List<TodoEntity> entities = find("parent.id", parentId).list();
        for (TodoEntity entity : entities) {
            Hibernate.initialize(entity.getCategories());
        }
        return entities.stream()
                .map(TodoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<Todo> update(UUID id, Todo changes) {
        return findByIdOptional(id).map(entity -> {
            if (changes.getTitle() != null) entity.setTitle(changes.getTitle());
            if (changes.getDescription() != null) entity.setDescription(changes.getDescription());
            if (changes.getDueDate() != null) entity.setDueDate(changes.getDueDate());
            if (changes.getPriority() != null) entity.setPriority(changes.getPriority());
            if (changes.getUpdatedAt() != null) entity.setUpdatedAt(changes.getUpdatedAt());
            entity.setCompleted(changes.isCompleted());
            return TodoMapper.toDomain(entity);
        });
    }

    @Override
    @Transactional
    public boolean deleteById(UUID id) {
        return findByIdOptional(id).map(entity -> {
            delete(entity);
            return true;
        }).orElse(false);
    }
}
