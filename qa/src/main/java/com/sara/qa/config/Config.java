package com.sara.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Configuracion centralizada: lee de config.properties y permite override
 * por system property (-Dbrowser=edge) o por variable de entorno (QA_BROWSER).
 */
public final class Config {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) throw new IllegalStateException("config.properties no encontrado en classpath");
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Fallo al cargar config.properties", e);
        }
    }

    private Config() {
    }

    public static String get(String key) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null) return fromSystem;

        String envKey = "QA_"
                + key.toUpperCase(Locale.ROOT)
                        .replace('.', '_')
                        .replace('-', '_');
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null) return fromEnv;

        return props.getProperty(key);
    }

    public static String appBaseUrl() {
        return get("app.baseUrl");
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static int timeoutSeconds() {
        return Integer.parseInt(get("timeout.seconds"));
    }
}