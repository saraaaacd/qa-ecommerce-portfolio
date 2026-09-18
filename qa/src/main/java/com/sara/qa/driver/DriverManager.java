package com.sara.qa.driver;

import com.sara.qa.config.Config;
import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver start() {
        WebDriver current = DriverFactory.create(Config.browser(), Config.headless());
        driver.set(current);
        return current;
    }

    public static WebDriver get() {
        WebDriver current = driver.get();
        if (current == null) {
            throw new IllegalStateException("Driver no iniciado. Llama a DriverManager.start() antes.");
        }
        return current;
    }

    public static void stop() {
        WebDriver current = driver.get();
        if (current != null) {
            current.quit();
            driver.remove();
        }
    }
}