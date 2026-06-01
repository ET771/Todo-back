package org.acme.application.usecase;

import org.acme.application.dto.AddCommentDto;
import org.acme.domain.models.Comment;
import org.acme.domain.models.User;
import org.acme.domain.repository.CommentRepository;
import org.acme.infrastructure.security.AuthContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddCommentToTodoUseCaseTest {

    private CommentRepository commentRepository;
    private AuthContext authContext;
    private AddCommentToTodoUseCase useCase;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        authContext = mock(AuthContext.class);

        User user = new User(UUID.randomUUID(), "test@test.com", "Test User", "USER", true, "firebase-uid");
        when(authContext.getUser()).thenReturn(user);
        when(commentRepository.save(any(Comment.class), any(UUID.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        useCase = new AddCommentToTodoUseCase(commentRepository, authContext);
    }

    @Test
    void executeShouldCreateCommentWithAuthorEmailAndTimestamp() {
        UUID todoId = UUID.randomUUID();
        AddCommentDto dto = new AddCommentDto();
        dto.setContent("Great task!");

        Comment result = useCase.execute(todoId, dto);

        assertNotNull(result.getId());
        assertEquals("Great task!", result.getContent());
        assertEquals("test@test.com", result.getAuthorEmail());
        assertNotNull(result.getCreatedAt());
    }
}
