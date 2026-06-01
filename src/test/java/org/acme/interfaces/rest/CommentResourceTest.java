package org.acme.interfaces.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class CommentResourceTest {

    // Seed IDs from import.sql
    private static final String TODO_ID = "bbbbbbb1-0000-0000-0000-000000000001";

    @Test
    void getCommentsShouldReturnSeedComments() {
        given()
                .header("Authorization", "Bearer fake-token")
                .when().get("/todos/" + TODO_ID + "/comments")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(2)))
                .body("[0].content", notNullValue());
    }

    @Test
    void addCommentShouldPersistAndReturnIt() {
        String body = "{\"content\":\"Nuevo comentario de prueba\"}";

        given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .body(body)
                .when().post("/todos/" + TODO_ID + "/comments")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("content", equalTo("Nuevo comentario de prueba"))
                .body("authorEmail", equalTo("test@test.com"))
                .body("createdAt", notNullValue());
    }

    @Test
    void deleteCommentShouldReturn204() {
        String body = "{\"content\":\"Para borrar\"}";

        String commentId = given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .body(body)
                .when().post("/todos/" + TODO_ID + "/comments")
                .then().statusCode(201)
                .extract().jsonPath().getString("id");

        given()
                .header("Authorization", "Bearer fake-token")
                .when().delete("/todos/" + TODO_ID + "/comments/" + commentId)
                .then().statusCode(204);
    }
}
