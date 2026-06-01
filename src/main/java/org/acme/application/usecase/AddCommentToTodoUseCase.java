package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.dto.AddCommentDto;
import org.acme.domain.models.Comment;
import org.acme.domain.repository.CommentRepository;
import org.acme.infrastructure.security.AuthContext;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class AddCommentToTodoUseCase {

    private final CommentRepository commentRepository;
    private final AuthContext authContext;

    @Inject
    public AddCommentToTodoUseCase(CommentRepository commentRepository, AuthContext authContext) {
        this.commentRepository = commentRepository;
        this.authContext = authContext;
    }

    public Comment execute(UUID todoId, AddCommentDto dto) {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthorEmail(authContext.getUser().getEmail());
        return commentRepository.save(comment, todoId);
    }
}
