package com.github.budwing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigLoader {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("app.properties")) {
            if (input != null) {
                props.load(input);
                log.info("load app.properties success");
            } else {
                log.warn("app.properties not found");
            }
        } catch (IOException e) {
            log.error("load app.properties failed", e);
        }
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
