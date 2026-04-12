package com.nidhinsai.api.tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import com.nidhinsai.api.models.UserPayload;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

@Feature("Users API")
@Test(groups = "users")
public class UsersApiTest extends BaseApiTest {

    @Test(description = "GET /api/users returns 200 and valid JSON schema")
    @Story("List Users")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that the users list endpoint returns HTTP 200 and a body conforming to the JSON schema.")
    public void shouldFetchUsersList() {
        log.info("TEST: Fetch users list page=2 - GET /api/users?page=2");
        given().spec(spec())
                .queryParam("page", 2)
        .when()
                .get("/api/users")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/users-list-schema.json"))
                .body("page",        equalTo(2))
                .body("data.size()", greaterThan(0));
        log.info("TEST PASS: users list schema validated");
    }

    @Test(description = "GET /api/users?page=1 returns 200 with first page data")
    @Story("List Users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates the first page of the user list is returned correctly.")
    public void shouldFetchFirstPageOfUsers() {
        log.info("TEST: Fetch users list page=1 - GET /api/users?page=1");
        given().spec(spec())
                .queryParam("page", 1)
        .when()
                .get("/api/users")
        .then()
                .statusCode(200)
                .body("page",        equalTo(1))
                .body("data.size()", greaterThan(0))
                .body("per_page",    notNullValue())
                .body("total",       greaterThan(0));
        log.info("TEST PASS: first page of users returned");
    }

    @Test(description = "POST /api/users creates a new user and returns 201")
    @Story("Create User")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that a new user can be created via POST and the response contains id and createdAt.")
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
    @Story("Get Single User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates that a single existing user is returned with correct fields and matches the JSON schema.")
    public void shouldGetSingleUser() {
        log.info("TEST: Get single user - GET /api/users/2");
        given().spec(spec())
        .when()
                .get("/api/users/2")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/single-user-schema.json"))
                .body("data.id",         equalTo(2))
                .body("data.email",      notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name",  notNullValue())
                .body("data.avatar",     notNullValue());
        log.info("TEST PASS: single user schema validated");
    }

    @Test(description = "GET /api/users/{id} returns 404 for non-existent user")
    @Story("Get Single User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that requesting a non-existent user returns HTTP 404.")
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
    @Story("Update User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies a full update of a user resource via PUT returns 200 and updated fields.")
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
    @Story("Update User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies a partial update of a user resource via PATCH returns 200 and the updated field.")
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
    @Story("Delete User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that deleting a user returns HTTP 204 with no body.")
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
