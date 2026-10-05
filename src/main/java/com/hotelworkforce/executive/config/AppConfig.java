package com.hotelworkforce.executive.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException ignored) {
        }
    }

    private AppConfig() {}

    public static String get(String envKey, String propertyKey, String defaultValue) {
        String env = System.getenv(envKey);
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        return PROPS.getProperty(propertyKey, defaultValue).trim();
    }

    public static boolean getBoolean(String envKey, String propertyKey, boolean defaultValue) {
        String value = get(envKey, propertyKey, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    public static String appMode() {
        return get("APP_MODE", "app.mode", "DEV");
    }
}
