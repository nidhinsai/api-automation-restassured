package com.nidhinsai.api.utils;

import com.nidhinsai.api.filters.RequestResponseLoggingFilter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Fetches and caches a bearer token for the duration of the test run.
 *
 * Targets the reqres.in demo auth endpoint by default.
 * Override via system properties: test.auth.email / test.auth.password
 */
public final class TokenManager {

    private static final Logger LOG = LogManager.getLogger(TokenManager.class);

    /** Volatile ensures visibility across threads without synchronization overhead for reads. */
    private static volatile String cachedToken;
    private static final Object LOCK = new Object();

    private TokenManager() {
    }

    /**
     * Returns a cached token, fetching one if not yet obtained.
     * Thread-safe via double-checked locking.
     */
    public static String getToken() {
        if (cachedToken == null) {
            synchronized (LOCK) {
                if (cachedToken == null) {
                    cachedToken = fetchToken();
                }
            }
        }
        return cachedToken;
    }

    /** Forces a fresh token fetch (call after logout or token expiry). */
    public static void invalidate() {
        synchronized (LOCK) {
            LOG.info("Token cache invalidated");
            cachedToken = null;
        }
    }

    // ── private ───────────────────────────────────────────────────────────────

    private static String fetchToken() {
        String baseUri  = ConfigManager.get("base.url",       "https://reqres.in");
        String email    = ConfigManager.get("test.auth.email",    "eve.holt@reqres.in");
        String password = ConfigManager.get("test.auth.password", "cityslicka");

        LOG.info("Fetching auth token from {}/api/login for user '{}'", baseUri, email);

        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        Response response = RestAssured.given()
                .filter(new RequestResponseLoggingFilter())
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/login");

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException(
                    "Auth token fetch failed — HTTP " + response.getStatusCode()
                    + " body: " + response.getBody().asString());
        }

        String token = response.jsonPath().getString("token");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Auth response did not contain a token: " + response.getBody().asString());
        }

        LOG.info("Auth token obtained successfully (length={})", token.length());
        return token;
    }
}
