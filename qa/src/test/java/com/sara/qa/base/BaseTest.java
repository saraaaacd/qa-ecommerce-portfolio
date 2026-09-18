package com.sara.qa.base;

import com.sara.qa.driver.DriverManager;
import com.sara.qa.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public abstract class BaseTest {

    @RegisterExtension
    protected Watchman watchman = new Watchman();

    @BeforeEach
    void setUpDriver() {
        DriverManager.start();
    }

    @AfterEach
    void tearDownDriver() {
        if (watchman.failed()) {
            attachScreenshot("Fallo en el test");
        }
        DriverManager.stop();
    }

    protected WebDriver driver() {
        return DriverManager.get();
    }

    protected void attachScreenshot(String name) {
        try {
            byte[] png = ScreenshotUtils.capturePng(driver());
            Allure.getLifecycle().addAttachment(name, "image/png", "png", png);
        } catch (RuntimeException ignored) {
        }
    }

    /**
     * Registra si el test fallo para poder adjuntar screenshot en @AfterEach.
     */
    public static class Watchman implements TestWatcher {

        private boolean failed;

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            failed = true;
        }

        public boolean failed() {
            return failed;
        }
    }
}