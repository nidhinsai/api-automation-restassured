package com.nidhinsai.api.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RestAssured filter that:
 * 1. Logs every HTTP request and response to Log4j2 (traffic appender).
 * 2. Stores the last request + response for the current thread so that
 *    {@link com.nidhinsai.api.listeners.ApiTestListener} can dump the full
 *    HTTP conversation when a test fails.
 */
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger LOG = LogManager.getLogger(RequestResponseLoggingFilter.class);

    /** Last captured HTTP traffic per test thread, available to the failure listener. */
    private static final ThreadLocal<String> LAST_TRAFFIC = new ThreadLocal<>();

    public static String getLastTraffic() {
        return LAST_TRAFFIC.get();
    }

    public static void clearLastTraffic() {
        LAST_TRAFFIC.remove();
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        // ── REQUEST ──────────────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════ HTTP REQUEST ══════════════╗\n");
        sb.append("  Method  : ").append(requestSpec.getMethod()).append("\n");
        sb.append("  URI     : ").append(requestSpec.getURI()).append("\n");
        sb.append("  Headers :\n");
        requestSpec.getHeaders().forEach(h ->
                sb.append("    ").append(h.getName()).append(": ").append(h.getValue()).append("\n"));
        Object body = requestSpec.getBody();
        if (body != null) {
            sb.append("  Body    :\n").append(body).append("\n");
        }
        sb.append("╚══════════════════════════════════════════╝");
        LOG.debug("{}", sb);

        // ── EXECUTE ──────────────────────────────────────────────────────────
        Response response = ctx.next(requestSpec, responseSpec);

        // ── RESPONSE ─────────────────────────────────────────────────────────
        StringBuilder rb = new StringBuilder();
        rb.append("\n╔══════════════ HTTP RESPONSE ═════════════╗\n");
        rb.append("  Status  : ").append(response.getStatusCode())
          .append(" ").append(response.getStatusLine()).append("\n");
        rb.append("  Time    : ").append(response.getTime()).append(" ms\n");
        rb.append("  Headers :\n");
        response.getHeaders().forEach(h ->
                rb.append("    ").append(h.getName()).append(": ").append(h.getValue()).append("\n"));
        String responseBody = response.getBody().asString();
        if (responseBody != null && !responseBody.isBlank()) {
            // Truncate very large payloads (e.g. binary download) in logs
            String displayed = responseBody.length() > 4096
                    ? responseBody.substring(0, 4096) + "\n  [... truncated]"
                    : responseBody;
            rb.append("  Body    :\n").append(displayed).append("\n");
        }
        rb.append("╚══════════════════════════════════════════╝");
        LOG.debug("{}", rb);

        // Store combined traffic for failure listener
        LAST_TRAFFIC.set(sb + "\n" + rb);

        return response;
    }
}
