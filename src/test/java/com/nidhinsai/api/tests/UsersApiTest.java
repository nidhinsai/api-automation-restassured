package com.nidhinsai.api.tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import com.nidhinsai.api.models.UserPayload;
import org.testng.annotations.Test;

@Test(groups = "users")
public class UsersApiTest extends BaseApiTest {

    @Test(description = "GET /api/users returns 200 and valid JSON schema")
    public void shouldFetchUsersList() {
        log.info("TEST: Fetch users list - GET /api/users?page=2");
        given().spec(spec())
                .queryParam("page", 2)
        .when()
                .get("/api/users")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/users-list-schema.json"))
                .body("page",       equalTo(2))
                .body("data.size()", greaterThan(0));
        log.info("TEST PASS: users list schema validated");
    }

    @Test(description = "POST /api/users creates a new user and returns 201")
    public void shouldCreateUser() {
        log.info("TEST: Create user - POST /api/users");
        UserPayload payload = new UserPayload("Nidhin", "QA Architect");

        given().spec(spec())
                .body(payload)
        .when()
                .post("/api/users")
        .then()
                .statusCode(201)
                .body("name",      equalTo("Nidhin"))
                .body("job",       equalTo("QA Architect"))
                .body("id",        notNullValue())
                .body("createdAt", notNullValue());
        log.info("TEST PASS: user created successfully");
    }

    @Test(description = "GET /api/users/{id} returns 200 for existing user")
    public void shouldGetSingleUser() {
        log.info("TEST: Get single user - GET /api/users/2");
        given().spec(spec())
        .when()
                .get("/api/users/2")
        .then()
                .statusCode(200)
                .body("data.id",         equalTo(2))
                .body("data.email",      notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name",  notNullValue());
        log.info("TEST PASS: single user validated");
    }

    @Test(description = "GET /api/users/{id} returns 404 for non-existent user")
    public void shouldReturn404ForMissingUser() {
        log.info("TEST: Expect 404 - GET /api/users/9999");
        given().spec(spec())
        .when()
                .get("/api/users/9999")
        .then()
                .statusCode(404);
        log.info("TEST PASS: 404 for missing user confirmed");
    }

    @Test(description = "PUT /api/users/{id} updates user and returns 200")
    public void shouldUpdateUser() {
        log.info("TEST: Update user - PUT /api/users/2");
        UserPayload payload = new UserPayload("Nidhin Updated", "Senior QA Architect");

        given().spec(spec())
                .body(payload)
        .when()
                .put("/api/users/2")
        .then()
                .statusCode(200)
                .body("name",      equalTo("Nidhin Updated"))
                .body("job",       equalTo("Senior QA Architect"))
                .body("updatedAt", notNullValue());
        log.info("TEST PASS: user updated successfully");
    }

    @Test(description = "PATCH /api/users/{id} partially updates user")
    public void shouldPatchUser() {
        log.info("TEST: Partial update - PATCH /api/users/2");
        UserPayload patch = new UserPayload(null, "Principal Engineer");

        given().spec(spec())
                .body(patch)
        .when()
                .patch("/api/users/2")
        .then()
                .statusCode(200)
                .body("job",       equalTo("Principal Engineer"))
                .body("updatedAt", notNullValue());
        log.info("TEST PASS: user patched successfully");
    }

    @Test(description = "DELETE /api/users/{id} returns 204")
    public void shouldDeleteUser() {
        log.info("TEST: Delete user - DELETE /api/users/2");
        given().spec(spec())
        .when()
                .delete("/api/users/2")
        .then()
                .statusCode(204);
        log.info("TEST PASS: user deleted, 204 received");
    }
}
