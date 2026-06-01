package org.acme.infrastructure.mapper;

import org.acme.domain.models.Todo;
import org.acme.infrastructure.entities.CategoryEntity;
import org.acme.infrastructure.entities.CommentEntity;
import org.acme.infrastructure.entities.TodoEntity;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class TodoMapper {

    public static Todo toDomain(TodoEntity entity) {
        Todo todo = new Todo();
        todo.setId(entity.getId());
        todo.setTitle(entity.getTitle());
        todo.setDescription(entity.getDescription());
        todo.setCompleted(entity.isCompleted());
        todo.setCreatedAt(entity.getCreatedAt());
        todo.setUpdatedAt(entity.getUpdatedAt());
        todo.setDueDate(entity.getDueDate());
        todo.setPriority(entity.getPriority());

        if (entity.getOwner() != null && Hibernate.isInitialized(entity.getOwner())) {
            todo.setOwner(UserMapper.toDomain(entity.getOwner()));
        }

        if (entity.getParent() != null && Hibernate.isInitialized(entity.getParent())) {
            todo.setParentId(entity.getParent().getId());
        } else if (entity.getParent() != null) {
            // proxy not initialized - still expose the id via the join column
            todo.setParentId(entity.getParent().getId());
        }

        if (Hibernate.isInitialized(entity.getTasks())) {
            todo.setTasks(
                entity.getTasks().stream()
                    .map(TodoMapper::toDomain)
                    .collect(Collectors.toCollection(ArrayList::new))
            );
        }

        if (Hibernate.isInitialized(entity.getCategories())) {
            todo.setCategories(
                    entity.getCategories().stream()
                            .map(CategoryMapper::toDomain)
                            .collect(Collectors.toCollection(HashSet::new))
            );
        }

        if (Hibernate.isInitialized(entity.getComments())) {
            todo.setComments(
                    entity.getComments().stream()
                            .map(CommentMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }

        return todo;
    }

    public static TodoEntity toEntity(Todo todo) {
        TodoEntity entity = new TodoEntity();
        entity.setId(todo.getId());
        entity.setTitle(todo.getTitle());
        entity.setDescription(todo.getDescription());
        entity.setCompleted(todo.isCompleted());
        entity.setCreatedAt(todo.getCreatedAt());
        entity.setUpdatedAt(todo.getUpdatedAt());
        entity.setDueDate(todo.getDueDate());
        entity.setPriority(todo.getPriority());
        return entity;
    }
}