package org.acme.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.acme.domain.models.User;
import org.acme.domain.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class FirebaseAuthFilter implements ContainerRequestFilter {
    @Inject
    UserRepository userRepository;
    @Inject
    AuthContext authContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }
        String path=requestContext.getUriInfo().getPath();
        System.out.println(path);
        if(path.equals("/users") || path.equals("/status")){
            return;
        }

        String authHeader = requestContext.getHeaders().getFirst("Authorization");
        if(authHeader==null){
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("No autorizado").build()
            );
            return;
        }
        if(!authHeader.startsWith("Bearer ")){
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("No autorizado").build()
            );
            return;
        }
        String token = authHeader.substring("Bearer ".length());
        try {
            FirebaseToken decodedToken= FirebaseAuth
                    .getInstance()
                    .verifyIdToken(token,true);
            Optional<User> userOptional= userRepository.findByFirebaseUuid(decodedToken.getUid());
            if(userOptional.isEmpty()){
                requestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity("No autorizado").build()
                );
                return;
            }
            User user= userOptional.get();
            authContext.setUser(user);

        } catch (FirebaseAuthException e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("No autorizado").build()
            );

        }

    }
}