package org.acme.interfaces.rest;

import io.quarkus.test.Mock;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Provider;
import org.acme.domain.models.User;
import org.acme.infrastructure.security.AuthContext;
import org.acme.infrastructure.security.FirebaseAuthFilter;

import java.util.UUID;

/**
 * Test-only replacement for {@link FirebaseAuthFilter}.
 * Bypasses Firebase token verification and injects a fake authenticated user
 * into the {@link AuthContext} so secured endpoints can be exercised against the
 * real H2-backed use cases and repositories.
 */
@Mock
@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class TestFirebaseAuthFilter extends FirebaseAuthFilter {

    public static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final String TEST_FIREBASE_UID = "test-firebase-uid";

    @Inject
    AuthContext authContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        User user = new User(
                TEST_USER_ID,
                "test@test.com",
                "Test User",
                "USER",
                true,
                TEST_FIREBASE_UID
        );
        authContext.setUser(user);
    }
}
