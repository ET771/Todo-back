package org.acme.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.application.usecase.demo.TodoFetchDemoUseCase;
import org.acme.domain.models.demo.TodoDemoResult;

import java.util.List;

@Path("/todo/demo")
@Produces(MediaType.APPLICATION_JSON)
public class TodoDemoResource {

    @Inject
    TodoFetchDemoUseCase useCase;

    @GET
    @Path("/lazy-ok")
    public Response lazyOk() {
        return Response.ok(useCase.lazyOk()).build();
    }

    @GET
    @Path("/eager-simulation")
    public Response eagerSimulation() {
        return Response.ok(useCase.eagerSimulation()).build();
    }

    @GET
    @Path("/n-plus-1")
    public Response nPlusOne() {
        return Response.ok(useCase.nPlusOne()).build();
    }

    @GET
    @Path("/join-fetch")
    public Response joinFetch() {
        return Response.ok(useCase.joinFetch()).build();
    }

    @GET
    @Path("/entity-graph")
    public Response entityGraph() {
        return Response.ok(useCase.entityGraph()).build();
    }

    @GET
    @Path("/projection")
    public Response projection() {
        return Response.ok(useCase.projection()).build();
    }

    @GET
    @Path("/lazy-init-exception")
    public Response lazyInitException() {
        return Response.ok(useCase.lazyInitException()).build();
    }

    @GET
    @Path("/all")
    public Response all() {
        List<TodoDemoResult> all = List.of(
                useCase.lazyOk(),
                useCase.eagerSimulation(),
                useCase.nPlusOne(),
                useCase.joinFetch(),
                useCase.entityGraph(),
                useCase.projection(),
                useCase.lazyInitException()
        );
        return Response.ok(all).build();
    }
}
