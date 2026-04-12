package com.nidhinsai.api.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.util.Map;
import org.testng.annotations.Test;

/**
 * Tests for the /api/login and /api/register endpoints.
 * Uses Map payloads instead of raw JSON strings for maintainability.
 */
@Feature("Authentication API")
@Test(groups = "auth")
public class AuthApiTest extends BaseApiTest {

    @Test(description = "POST /api/login with valid credentials returns token")
    @Story("Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that a user with valid credentials receives an auth token.")
    public void shouldLoginWithValidCredentials() {
        log.info("TEST: Successful login — POST /api/login");
        given().spec(spec())
                .body(Map.of("email", "eve.holt@reqres.in", "password", "cityslicka"))
        .when()
                .post("/api/login")
        .then()
                .statusCode(200)
                .body("token", notNullValue());
        log.info("TEST PASS: auth token received");
    }

    @Test(description = "POST /api/login with missing password returns 400")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that omitting the password field returns HTTP 400 with a descriptive error.")
    public void shouldFailLoginWithMissingPassword() {
        log.info("TEST: Login with missing password — POST /api/login");
        given().spec(spec())
                .body(Map.of("email", "eve.holt@reqres.in"))
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
        log.info("TEST PASS: 400 returned for missing password");
    }

    @Test(description = "POST /api/login with missing email returns 400")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that omitting the email field returns HTTP 400 with a descriptive error.")
    public void shouldFailLoginWithMissingEmail() {
        log.info("TEST: Login with missing email — POST /api/login");
        given().spec(spec())
                .body(Map.of("password", "cityslicka"))
        .when()
                .post("/api/login")
        .then()
                .statusCode(400)
                .body("error", equalTo("Missing email or username"));
        log.info("TEST PASS: 400 returned for missing email");
    }

    @Test(description = "POST /api/register with valid data returns 200 and id+token")
    @Story("Registration")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that a supported user can register and receive an id and token.")
    public void shouldRegisterSuccessfully() {
        log.info("TEST: Successful registration — POST /api/register");
        given().spec(spec())
                .body(Map.of("email", "eve.holt@reqres.in", "password", "pistol"))
        .when()
                .post("/api/register")
        .then()
                .statusCode(200)
                .body("id",    notNullValue())
                .body("token", notNullValue());
        log.info("TEST PASS: registration successful");
    }

    @Test(description = "POST /api/register with missing password returns 400")
    @Story("Registration")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that incomplete registration data returns HTTP 400 with a descriptive error.")
    public void shouldFailRegistrationWithMissingPassword() {
        log.info("TEST: Registration missing password — POST /api/register");
        given().spec(spec())
                .body(Map.of("email", "sydney@fife"))
        .when()
                .post("/api/register")
        .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
        log.info("TEST PASS: 400 returned for incomplete registration");
    }
}
