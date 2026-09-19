package com.sara.qa.a11y;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.sara.qa.base.BaseTest;
import com.sara.qa.config.Config;
import com.sara.qa.pages.HomePage;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("a11y")
@DisplayName("Accesibilidad (axe-core)")
class AccessibilityTests extends BaseTest {

    private static final List<String> TAGS = List.of("wcag2a", "wcag2aa", "wcag21aa", "best-practice");

    private Results analyze(String url) {
        driver().get(url);
        return new AxeBuilder().withTags(TAGS).analyze(driver());
    }

    private void assertNoCriticalViolations(String pageName, Results results) {
        List<Rule> critical = results.getViolations().stream()
                .filter(v -> "critical".equalsIgnoreCase(String.valueOf(v.getImpact())))
                .toList();

        if (!critical.isEmpty()) {
            StringBuilder detail = new StringBuilder(
                    "Violaciones CRITICAS en " + pageName + ":\n");
            for (Rule v : critical) {
                detail.append("- [").append(v.getId()).append("] ")
                        .append(v.getHelp()).append("\n")
                        .append("  Nodos afectados: ").append(v.getNodes().size()).append("\n");
            }
            Allure.addAttachment("Violaciones criticas - " + pageName, detail.toString());
        }

        assertTrue(critical.isEmpty(),
                "Se encontraron " + critical.size() + " violaciones CRITICAS en " + pageName);
    }

    @Test
    @DisplayName("Home sin violaciones criticas WCAG")
    void homeHasNoCriticalViolations() {
        assertNoCriticalViolations("Home", analyze(Config.appBaseUrl()));
    }

    @Test
    @DisplayName("Carrito (con items) sin violaciones criticas WCAG")
    void cartWithItemsHasNoCriticalViolations() {
        HomePage home = new HomePage(driver()).open();
        home.addToCart("Ratón inalámbrico");

        assertNoCriticalViolations("Cart", analyze(Config.appBaseUrl() + "/cart"));
    }

    @Test
    @DisplayName("Formulario de checkout sin violaciones criticas WCAG")
    void checkoutHasNoCriticalViolations() {
        HomePage home = new HomePage(driver()).open();
        home.addToCart("Ratón inalámbrico");
        home.openCart().checkout();

        assertNoCriticalViolations("Checkout", analyze(Config.appBaseUrl() + "/checkout"));
    }
}