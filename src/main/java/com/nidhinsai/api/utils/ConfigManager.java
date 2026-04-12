package com.nidhinsai.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread-safe, immutable config reader.
 * Load order: classpath → file-system → System property override.
 */
public final class ConfigManager {

    private static final Logger LOG = LogManager.getLogger(ConfigManager.class);
    private static final Properties PROPERTIES = new Properties();

    static {
        String resourcePath = "config/config.properties";

        // 1. Try classpath first (jar-safe, CI-safe)
        try (InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                PROPERTIES.load(is);
                LOG.debug("Loaded api config from classpath: {}", resourcePath);
            }
        } catch (IOException e) {
            LOG.warn("IOException reading classpath resource '{}': {}", resourcePath, e.getMessage());
        }

        // 2. Fall back to file-system (project-root relative — local runs)
        if (PROPERTIES.isEmpty()) {
            java.nio.file.Path fsPath = Paths.get(resourcePath);
            if (Files.exists(fsPath)) {
                try (InputStream is = Files.newInputStream(fsPath)) {
                    PROPERTIES.load(is);
                    LOG.debug("Loaded api config from file-system: {}", fsPath.toAbsolutePath());
                } catch (IOException e) {
                    LOG.warn("IOException reading file-system resource '{}': {}", resourcePath, e.getMessage());
                }
            } else {
                LOG.warn("Api config not found at classpath or file-system: {}", resourcePath);
            }
        }

        LOG.info("ConfigManager initialised. base.url={}", PROPERTIES.getProperty("base.url", "(not set)"));
    }

    private ConfigManager() {
    }

    public static String get(String key, String defaultValue) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /** Throws if a required key is absent. */
    public static String getRequired(String key) {
        String value = get(key, null);
        if (value == null) {
            throw new IllegalStateException("Required config key not found: '" + key + "'");
        }
        return value;
    }
}
