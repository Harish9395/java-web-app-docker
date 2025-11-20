package com.example.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {

    private static final String ENV_FILE = "/app/config/app.env";
    private static Properties props = new Properties();

    static {
        try {
            props.load(new FileInputStream(ENV_FILE));
        } catch (IOException e) {
            System.err.println("Could not load app.env file: " + e.getMessage());
        }
    }

    public static String getUsername() {
        String value = System.getenv("USERNAME");
        return value != null ? value : props.getProperty("USERNAME", "default-user");
    }

    public static String getLogLevel() {
        String value = System.getenv("LOG_LEVEL");
        return value != null ? value : props.getProperty("LOG_LEVEL", "INFO");
    }

    public static boolean getFeatureFlag() {
        String value = System.getenv("FEATURE_FLAG");
        if (value != null) return Boolean.parseBoolean(value);
        return Boolean.parseBoolean(props.getProperty("FEATURE_FLAG", "false"));
    }
}
