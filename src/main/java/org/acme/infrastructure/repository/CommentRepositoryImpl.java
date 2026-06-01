package org.acme.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.models.Comment;
import org.acme.domain.repository.CommentRepository;
import org.acme.infrastructure.entities.CommentEntity;
import org.acme.infrastructure.entities.TodoEntity;
import org.acme.infrastructure.mapper.CommentMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommentRepositoryImpl implements CommentRepository, PanacheRepositoryBase<CommentEntity, UUID> {

    @Override
    @Transactional
    public Comment save(Comment comment, UUID todoId) {
        TodoEntity todo = getEntityManager().getReference(TodoEntity.class, todoId);
        CommentEntity entity = new CommentEntity();
        entity.setId(comment.getId());
        entity.setContent(comment.getContent());
        entity.setAuthorEmail(comment.getAuthorEmail());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setTodo(todo);
        persist(entity);
        return CommentMapper.toDomain(entity);
    }

    @Override
    public List<Comment> findByTodoId(UUID todoId) {
        return find("todo.id", todoId).stream()
                .map(CommentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Comment> findCommentById(UUID commentId) {
        return findByIdOptional(commentId).map(CommentMapper::toDomain);
    }

    @Override
    @Transactional
    public boolean deleteById(UUID commentId) {
        return findByIdOptional(commentId).map(entity -> {
            delete(entity);
            return true;
        }).orElse(false);
    }
}
