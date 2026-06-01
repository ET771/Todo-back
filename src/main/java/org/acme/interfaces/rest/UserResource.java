package org.acme.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.application.dto.RegisterUserDto;
import org.acme.application.usecase.RegisterUserUseCase;
import org.acme.infrastructure.security.AuthContext;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    RegisterUserUseCase registerUserUseCase;

    @Inject
    AuthContext authContext;

    public UserResource(RegisterUserUseCase registerUserUseCase, AuthContext authContext){
        this.registerUserUseCase = registerUserUseCase;
        this.authContext = authContext;
    }

    @POST
    public Response registerUser(@Valid RegisterUserDto registerUserDto) {
        try {
            return Response.ok(registerUserUseCase.execute(registerUserDto)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(409).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @Path("/test")
    @GET
    public Response testEndpoint(){
        System.out.println("En el endpoint: "+authContext.getUser().getId());
        return Response.ok("Hello").build();
    }
}