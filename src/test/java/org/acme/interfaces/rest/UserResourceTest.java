package org.acme.interfaces.rest;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.domain.models.User;
import org.acme.domain.repository.UserRepository;
import org.acme.infrastructure.firebase.FirebaseUserCreator;
import org.acme.infrastructure.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class UserResourceTest {

    @InjectMock
    FirebaseUserCreator firebaseUserCreator;

    @Inject
    UserRepository userRepository;

    @Test
    void registerUserShouldPersistUserInH2() throws FirebaseAuthException {
        String firebaseUid = "firebase-uid-" + UUID.randomUUID();
        String email = "alice-" + UUID.randomUUID() + "@test.com";

        UserRecord firebaseRecord = Mockito.mock(UserRecord.class);
        Mockito.when(firebaseRecord.getUid()).thenReturn(firebaseUid);
        Mockito.when(firebaseUserCreator.create(Mockito.eq(email), Mockito.eq("Supersecret2")))
                .thenReturn(firebaseRecord);

        String body = "{\"fullName\":\"Alice Smith\",\"email\":\"" + email + "\",\"password\":\"Supersecret2\"}";

        given()
                .contentType(JSON)
                .body(body)
                .when().post("/users")
                .then()
                .statusCode(200)
                .body("email", equalTo(email))
                .body("fullName", equalTo("Alice Smith"))
                .body("role", equalTo("USER"))
                .body("active", equalTo(true))
                .body("firebaseUuid", equalTo(firebaseUid))
                .body("id", notNullValue());

        // Verify the user was actually persisted in H2
        Optional<User> persisted = userRepository.findByFirebaseUuid(firebaseUid);
        assertTrue(persisted.isPresent(), "User should be persisted in H2");
        assertEquals(email, persisted.get().getEmail());
        assertEquals("Alice Smith", persisted.get().getFullName());
        assertEquals("USER", persisted.get().getRole());
        assertTrue(persisted.get().isActive());
    }

    @Test
    void registerUserShouldReturn500WhenFirebaseFails() throws FirebaseAuthException {
        FirebaseAuthException ex = Mockito.mock(FirebaseAuthException.class);
        Mockito.when(ex.getMessage()).thenReturn("firebase down");
        Mockito.when(firebaseUserCreator.create(Mockito.any(), Mockito.any())).thenThrow(ex);

        String body = "{\"fullName\":\"Bob\",\"email\":\"bob@test.com\",\"password\":\"Password123\"}";

        given()
                .contentType(JSON)
                .body(body)
                .when().post("/users")
                .then()
                .statusCode(500);
    }

    @Test
    void registerUserShouldReturn400InvalidName() throws FirebaseAuthException {

        String body = "{\"fullName\":\"\",\"email\":\"bob@test.com\",\"password\":\"Password123\"}";

        given()
                .contentType(JSON)
                .body(body)
                .when().post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    void registerUserShouldReturn400InvalidEmail() throws FirebaseAuthException {
        String body = "{\"fullName\":\"\"Bob,\"email\"bob@test.com:\"\",\"password\":\"Password123\"}";

        given()
                .contentType(JSON)
                .body(body)
                .when().post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    void registerUserShouldReturn400InvalidPassword() throws FirebaseAuthException {
        String body = "{\"fullName\":\"\"Bob,\"email\"bob@test.com:\"\",\"password\":\"Pass\"}";

        given()
                .contentType(JSON)
                .body(body)
                .when().post("/users")
                .then()
                .statusCode(400);
    }
}
