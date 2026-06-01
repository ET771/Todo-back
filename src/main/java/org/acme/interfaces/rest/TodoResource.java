package org.acme.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import java.util.Map;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.application.dto.CreateTodoDto;
import org.acme.application.dto.UpdateTodoDto;
import org.acme.application.usecase.CreateTodoUseCase;
import org.acme.application.usecase.DeleteTodoUseCase;
import org.acme.application.usecase.GetAllTodosUseCase;
import org.acme.application.usecase.GetTasksByListUseCase;
import org.acme.application.usecase.GetTodoByIdUseCase;
import org.acme.application.usecase.AddCategoryToTodoUseCase;
import org.acme.application.usecase.RemoveCategoryFromTodoUseCase;
import org.acme.application.usecase.SearchTodosUseCase;
import org.acme.application.usecase.UpdateTodoUseCase;
import org.acme.domain.models.Todo;

import java.util.List;

import java.util.UUID;

@Path("/todos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

    private final CreateTodoUseCase createTodoUseCase;
    private final GetTodoByIdUseCase getTodoByIdUseCase;
    private final GetAllTodosUseCase getAllTodosUseCase;
    private final GetTasksByListUseCase getTasksByListUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;
    private final UpdateTodoUseCase updateTodoUseCase;
    private final AddCategoryToTodoUseCase addCategoryToTodoUseCase;
    private final RemoveCategoryFromTodoUseCase removeCategoryFromTodoUseCase;
    private final SearchTodosUseCase searchTodosUseCase;

    @Inject
    public TodoResource(CreateTodoUseCase createTodoUseCase, GetTodoByIdUseCase getTodoByIdUseCase,
                        GetAllTodosUseCase getAllTodosUseCase, GetTasksByListUseCase getTasksByListUseCase,
                        DeleteTodoUseCase deleteTodoUseCase, UpdateTodoUseCase updateTodoUseCase,
                        AddCategoryToTodoUseCase addCategoryToTodoUseCase,
                        RemoveCategoryFromTodoUseCase removeCategoryFromTodoUseCase,
                        SearchTodosUseCase searchTodosUseCase) {
        this.createTodoUseCase = createTodoUseCase;
        this.getTodoByIdUseCase = getTodoByIdUseCase;
        this.getAllTodosUseCase = getAllTodosUseCase;
        this.getTasksByListUseCase = getTasksByListUseCase;
        this.deleteTodoUseCase = deleteTodoUseCase;
        this.updateTodoUseCase = updateTodoUseCase;
        this.addCategoryToTodoUseCase = addCategoryToTodoUseCase;
        this.removeCategoryFromTodoUseCase = removeCategoryFromTodoUseCase;
        this.searchTodosUseCase = searchTodosUseCase;
    }

    @POST
    public Response createTodo(CreateTodoDto todoDto) {
        Todo todo= createTodoUseCase.execute(todoDto);
        return Response.ok(todo).build();
    }

    @GET
    @Path("/{id}")
    public Response getTodoById(@PathParam("id") UUID id){
        Todo todo = getTodoByIdUseCase.execute(id);
        return Response.ok(todo).build();
    }

    @GET
    @Path("/{id}/tasks")
    public Response getTasksByList(@PathParam("id") UUID id) {
        return Response.ok(getTasksByListUseCase.execute(id)).build();
    }

    @GET
    public Response getAllTodos() {
        List<Todo> todos = getAllTodosUseCase.execute();
        return Response.ok(todos).build();
    }

    @GET
    @Path("/search")
    public Response search(@QueryParam("q") String query) {
        if (query == null || query.isBlank()) {
            return Response.ok(List.of()).build();
        }
        return Response.ok(searchTodosUseCase.execute(query)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, UpdateTodoDto dto) {
        return Response.ok(updateTodoUseCase.execute(id, dto)).build();
    }

    @POST
    @Path("/{todoId}/categories/{categoryId}")
    public Response addCategory(@PathParam("todoId") UUID todoId, @PathParam("categoryId") UUID categoryId) {
        addCategoryToTodoUseCase.execute(todoId, categoryId);
        return Response.ok(Map.of("added", true)).build();
    }

    @DELETE
    @Path("/{todoId}/categories/{categoryId}")
    public Response removeCategory(@PathParam("todoId") UUID todoId, @PathParam("categoryId") UUID categoryId) {
        removeCategoryFromTodoUseCase.execute(todoId, categoryId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = deleteTodoUseCase.execute(id);
        return deleted
            ? Response.ok(Map.of("deleted", true)).build()
            : Response.status(404).build();
    }
}
