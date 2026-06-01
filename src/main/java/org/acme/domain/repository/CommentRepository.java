package org.acme.domain.repository;

import org.acme.domain.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {
    Comment save(Comment comment, UUID todoId);
    List<Comment> findByTodoId(UUID todoId);
    boolean deleteById(UUID commentId);
    Optional<Comment> findCommentById(UUID commentId);
}
