package com.nidhinsai.api.tests;

import com.nidhinsai.api.listeners.ApiTestListener;
import com.nidhinsai.api.utils.RequestSpecFactory;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

/**
 * Base class for all API test classes.
 *
 * Design notes:
 * - Each test class gets its own RequestSpecification instance via {@link #spec()}.
 * - We deliberately do NOT set RestAssured.requestSpecification (the static global),
 *   because that creates race conditions when tests run in parallel threads.
 * - Test classes call {@code given().spec(spec())} to attach the spec per request.
 */
@Listeners(ApiTestListener.class)
public abstract class BaseApiTest {

    protected final Logger log = LogManager.getLogger(getClass());

    private RequestSpecification requestSpec;

    @BeforeClass
    public void setUp() {
        log.info("Setting up request specification for {}", getClass().getSimpleName());
        requestSpec = RequestSpecFactory.defaultSpec();
    }

    /** Returns the per-instance RequestSpecification. Use in {@code given().spec(spec())}. */
    protected RequestSpecification spec() {
        return requestSpec;
    }

    /** Returns an authorized spec using the cached token. */
    protected RequestSpecification authorizedSpec() {
        return RequestSpecFactory.authorizedSpec(
                com.nidhinsai.api.utils.TokenManager.getToken());
    }
}
