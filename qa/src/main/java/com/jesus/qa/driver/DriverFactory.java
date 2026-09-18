package com.jesus.qa.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Locale;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create(String browser, boolean headless) {
        return switch (browser.toLowerCase(Locale.ROOT)) {
            case "chrome" -> chrome(headless);
            case "edge" -> edge(headless);
            case "firefox" -> firefox(headless);
            default -> throw new IllegalArgumentException("Navegador no soportado: " + browser);
        };
    }

    private static ChromeDriver chrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage",
                "--window-size=1920,1080");
        if (headless) {
            opts.addArguments("--headless=new");
        }
        return new ChromeDriver(opts);
    }

    private static EdgeDriver edge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions opts = new EdgeOptions();
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage",
                "--window-size=1920,1080");
        if (headless) {
            opts.addArguments("--headless=new");
        }
        return new EdgeDriver(opts);
    }

    private static FirefoxDriver firefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions opts = new FirefoxOptions();
        if (headless) {
            opts.addArguments("-headless");
        }
        return new FirefoxDriver(opts);
    }
}