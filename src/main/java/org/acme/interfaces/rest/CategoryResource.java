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
import org.acme.application.dto.CreateCategoryDto;
import org.acme.application.usecase.CreateCategoryUseCase;
import org.acme.application.usecase.DeleteCategoryUseCase;
import org.acme.application.usecase.GetAllCategoriesUseCase;
import org.acme.application.usecase.GetCategoryByIdUseCase;

import java.util.UUID;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetAllCategoriesUseCase getAllCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    @Inject
    public CategoryResource(CreateCategoryUseCase createCategoryUseCase,
                            GetAllCategoriesUseCase getAllCategoriesUseCase,
                            GetCategoryByIdUseCase getCategoryByIdUseCase,
                            DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
        this.getCategoryByIdUseCase = getCategoryByIdUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @GET
    public Response getAll() {
        return Response.ok(getAllCategoriesUseCase.execute()).build();
    }

    @POST
    public Response create(CreateCategoryDto dto) {
        return Response.status(Response.Status.CREATED)
                .entity(createCategoryUseCase.execute(dto))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        return Response.ok(getCategoryByIdUseCase.execute(id)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        deleteCategoryUseCase.execute(id);
        return Response.noContent().build();
    }
}
