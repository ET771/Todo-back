package org.acme.interfaces.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.acme.infrastructure.entities.TodoEntity;
import org.acme.infrastructure.repository.TodoRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class TodoResourceTest {

    @Inject
    TodoRepositoryImpl todoRepository;

    @Test
    void createTodoShouldPersistInH2AndReturnIt() {
        String body = "{\"title\":\"Read a book\",\"description\":\"Clean Architecture\"}";

        Response response = given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .body(body)
                .when().post("/todos")
                .then()
                .statusCode(200)
                .body("title", equalTo("Read a book"))
                .body("description", equalTo("Clean Architecture"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();

        UUID returnedId = UUID.fromString(response.jsonPath().getString("id"));

        TodoEntity persisted = todoRepository.findById(returnedId);
        assertNotNull(persisted, "Todo should be persisted in H2");
        assertEquals(returnedId, persisted.getId());
        assertEquals("Read a book", persisted.getTitle());
        assertEquals("Clean Architecture", persisted.getDescription());
        assertNotNull(persisted.getCreatedAt());
    }

    @Test
    void updateShouldModifyTodoFields() {
        String createBody = "{\"title\":\"Original\",\"description\":\"Original desc\"}";

        String id = given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .body(createBody)
                .when().post("/todos")
                .then().statusCode(200)
                .extract().jsonPath().getString("id");

        String updateBody = "{\"title\":\"Updated\",\"description\":\"Updated desc\",\"completed\":true,\"priority\":\"HIGH\"}";

        given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .body(updateBody)
                .when().put("/todos/" + id)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated"))
                .body("description", equalTo("Updated desc"))
                .body("completed", equalTo(true))
                .body("priority", equalTo("HIGH"));
    }

    @Test
    void deleteShouldRemoveTheRequestedId() {
        String body1 = "{\"title\":\"a\",\"description\":\"desc a\"}";
        String body2 = "{\"title\":\"b\",\"description\":\"desc b\"}";

        String id1 = given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .body(body1)
                .when().post("/todos")
                .then().statusCode(200)
                .extract().jsonPath().getString("id");

        String id2 = given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .body(body2)
                .when().post("/todos")
                .then().statusCode(200)
                .extract().jsonPath().getString("id");

        given()
                .header("Authorization", "Bearer fake-token")
                .when().delete("/todos/" + id2)
                .then().statusCode(204);

        given()
                .header("Authorization", "Bearer fake-token")
                .when().get("/todos")
                .then()
                .body("id", hasItem(id1))
                .body("id", not(hasItem(id2)));
    }
}
