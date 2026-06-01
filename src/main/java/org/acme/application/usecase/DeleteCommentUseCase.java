package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.domain.repository.CommentRepository;

import java.util.UUID;

@ApplicationScoped
public class DeleteCommentUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public DeleteCommentUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void execute(UUID commentId) {
        boolean deleted = commentRepository.deleteById(commentId);
        if (!deleted) {
            throw new NotFoundException("Comment not found: " + commentId);
        }
    }
}
