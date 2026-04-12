package com.nidhinsai.api.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.annotations.Test;

/**
 * Tests for the /api/login and /api/logout endpoints.
 * Covers: valid login, invalid credentials, missing payload fields.
 */
@Test(groups = "auth")
public class AuthApiTest extends BaseApiTest {

    @Test(description = "POST /api/login with valid credentials returns token")
    public void shouldLoginWithValidCredentials() {
        log.info("TEST: Successful login — POST /api/login");
        given().spec(spec())
                .body("{\"email\":\"eve.holt@reqres.in\",\"password\":\"cityslicka\"}")
        .when()
                .post("/api/login")
        .then()
                .statusCode(200)
                .body("token", notNullValue());
        log.info("TEST PASS: auth token received");
    }

    @Test(description = "POST /api/login with missing password returns 400")
    public void shouldFailLoginWithMissingPassword() {
        log.info("TEST: Login with missing password — POST /api/login");
        given().spec(spec())
                .body("{\"email\":\"eve.holt@reqres.in\"}")
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
        log.info("TEST PASS: 400 returned for missing password");
    }

    @Test(description = "POST /api/login with missing email returns 400")
    public void shouldFailLoginWithMissingEmail() {
        log.info("TEST: Login with missing email — POST /api/login");
        given().spec(spec())
                .body("{\"password\":\"cityslicka\"}")
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("error", equalTo("Missing email or username"));
        log.info("TEST PASS: 400 returned for missing email");
    }

    @Test(description = "POST /api/register with valid data returns 200 and id+token")
    public void shouldRegisterSuccessfully() {
        log.info("TEST: Successful registration — POST /api/register");
        given().spec(spec())
                .body("{\"email\":\"eve.holt@reqres.in\",\"password\":\"pistol\"}")
        .when()
                .post("/api/register")
        .then()
                .statusCode(200)
                .body("id",    notNullValue())
                .body("token", notNullValue());
        log.info("TEST PASS: registration successful");
    }

    @Test(description = "POST /api/register with missing password returns 400")
    public void shouldFailRegistrationWithMissingPassword() {
        log.info("TEST: Registration missing password — POST /api/register");
        given().spec(spec())
                .body("{\"email\":\"sydney@fife\"}")
        .when()
                .post("/api/register")
        .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
        log.info("TEST PASS: 400 returned for incomplete registration");
    }
}
