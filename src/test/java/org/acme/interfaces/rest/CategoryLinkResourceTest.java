package org.acme.interfaces.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

@QuarkusTest
class CategoryLinkResourceTest {

    // Seed IDs from import.sql
    private static final String TODO_ID    = "bbbbbbb1-0000-0000-0000-000000000001";
    private static final String CAT_PERSONAL = "aaaaaaa2-0000-0000-0000-000000000002";

    @Test
    void addAndRemoveCategoryShouldUpdateTodoCategories() {
        // Add category
        given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .when().post("/todos/" + TODO_ID + "/categories/" + CAT_PERSONAL)
                .then().statusCode(204);

        given()
                .header("Authorization", "Bearer fake-token")
                .when().get("/todos/" + TODO_ID)
                .then()
                .statusCode(200)
                .body("categories.id", hasItem(CAT_PERSONAL));

        // Remove category
        given()
                .header("Authorization", "Bearer fake-token")
                .when().delete("/todos/" + TODO_ID + "/categories/" + CAT_PERSONAL)
                .then().statusCode(204);

        given()
                .header("Authorization", "Bearer fake-token")
                .when().get("/todos/" + TODO_ID)
                .then()
                .statusCode(200)
                .body("categories.id", not(hasItem(CAT_PERSONAL)));
    }

    @Test
    void addCategoryWithUnknownTodoShouldReturn404() {
        given()
                .contentType(JSON)
                .header("Authorization", "Bearer fake-token")
                .when().post("/todos/00000000-0000-0000-0000-000000000099/categories/" + CAT_PERSONAL)
                .then().statusCode(404);
    }
}
