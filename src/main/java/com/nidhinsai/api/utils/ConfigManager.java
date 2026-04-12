package com.nidhinsai.api.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigManager {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config/config.properties")) {
            PROPERTIES.load(fis);
        } catch (IOException ignored) {
        }
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
}