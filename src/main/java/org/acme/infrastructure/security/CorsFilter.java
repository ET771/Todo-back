package org.acme.infrastructure.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            request.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        String origin = request.getHeaderString("Origin");
        if (origin != null) {
            response.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
        } else {
            response.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        }
        response.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.getHeaders().putSingle("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        response.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
    }
}
