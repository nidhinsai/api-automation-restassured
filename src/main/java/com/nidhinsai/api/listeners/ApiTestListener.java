package com.nidhinsai.api.listeners;

import com.nidhinsai.api.filters.RequestResponseLoggingFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that fires on test events.
 * On failure: logs the exception + the full HTTP request/response
 * captured by {@link RequestResponseLoggingFilter}, and writes them
 * to a timestamped file inside {@code test-output/api-failures/}.
 */
public class ApiTestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(ApiTestListener.class);
    private static final String FAILURE_DIR = "test-output/api-failures";
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("▶ TEST START: {}.{}", result.getTestClass().getName(), result.getMethod().getMethodName());
        RequestResponseLoggingFilter.clearLastTraffic();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("✔ TEST PASS : {}.{} ({} ms)",
                result.getTestClass().getName(), result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis());
        RequestResponseLoggingFilter.clearLastTraffic();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
        long durationMs = result.getEndMillis() - result.getStartMillis();

        LOG.error("✘ TEST FAIL : {} ({} ms)", testName, durationMs);

        Throwable cause = result.getThrowable();
        if (cause != null) {
            LOG.error("  Exception : {}", cause.getMessage());
            LOG.debug("  Stack trace:", cause);
        }

        String traffic = RequestResponseLoggingFilter.getLastTraffic();
        if (traffic != null && !traffic.isBlank()) {
            LOG.error("  Last HTTP traffic for failed test:\n{}", traffic);
            writeFailureReport(testName, cause, traffic);
        } else {
            LOG.warn("  No HTTP traffic recorded for this test (was the filter registered?)");
        }

        RequestResponseLoggingFilter.clearLastTraffic();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("⊘ TEST SKIP : {}.{}", result.getTestClass().getName(), result.getMethod().getMethodName());
    }

    // ── private helper ───────────────────────────────────────────────────────

    private void writeFailureReport(String testName, Throwable cause, String traffic) {
        try {
            Path dir = Paths.get(FAILURE_DIR);
            Files.createDirectories(dir);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
            String safeName  = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path   file      = dir.resolve(timestamp + "_" + safeName + ".txt");

            StringBuilder report = new StringBuilder();
            report.append("TEST   : ").append(testName).append("\n");
            report.append("TIME   : ").append(LocalDateTime.now()).append("\n");
            if (cause != null) {
                report.append("ERROR  : ").append(cause.getMessage()).append("\n\n");
                report.append("STACK TRACE:\n");
                for (StackTraceElement el : cause.getStackTrace()) {
                    report.append("  at ").append(el).append("\n");
                }
            }
            report.append("\n").append(traffic);

            Files.writeString(file, report.toString(), StandardCharsets.UTF_8);
            LOG.info("  Failure report written → {}", file.toAbsolutePath());
        } catch (IOException e) {
            LOG.error("  Could not write failure report: {}", e.getMessage());
        }
    }
}
