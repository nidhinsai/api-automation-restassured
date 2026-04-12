package com.nidhinsai.api.tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import com.nidhinsai.api.models.UserPayload;
import org.testng.annotations.Test;

public class UsersApiTest extends BaseApiTest {

    @Test
    public void shouldFetchUsersList() {
        given()
                .queryParam("page", 2)
        .when()
                .get("/api/users")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/users-list-schema.json"));
    }

    @Test
    public void shouldCreateUser() {
        UserPayload payload = new UserPayload("Nidhin", "QA Architect");

        given()
                .body(payload)
        .when()
                .post("/api/users")
        .then()
                .statusCode(201);
    }
}