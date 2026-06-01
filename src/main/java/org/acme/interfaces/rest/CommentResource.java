package org.acme.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.application.dto.AddCommentDto;
import org.acme.application.usecase.AddCommentToTodoUseCase;
import org.acme.application.usecase.DeleteCommentUseCase;
import org.acme.domain.repository.CommentRepository;

import java.util.UUID;

@Path("/todos/{todoId}/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource {

    private final AddCommentToTodoUseCase addCommentToTodoUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final CommentRepository commentRepository;

    @Inject
    public CommentResource(AddCommentToTodoUseCase addCommentToTodoUseCase,
                           DeleteCommentUseCase deleteCommentUseCase,
                           CommentRepository commentRepository) {
        this.addCommentToTodoUseCase = addCommentToTodoUseCase;
        this.deleteCommentUseCase = deleteCommentUseCase;
        this.commentRepository = commentRepository;
    }

    @GET
    public Response getByTodo(@PathParam("todoId") UUID todoId) {
        return Response.ok(commentRepository.findByTodoId(todoId)).build();
    }

    @POST
    public Response add(@PathParam("todoId") UUID todoId, AddCommentDto dto) {
        return Response.status(Response.Status.CREATED)
                .entity(addCommentToTodoUseCase.execute(todoId, dto))
                .build();
    }

    @DELETE
    @Path("/{commentId}")
    public Response delete(@PathParam("todoId") UUID todoId, @PathParam("commentId") UUID commentId) {
        deleteCommentUseCase.execute(commentId);
        return Response.noContent().build();
    }
}
