package com.sara.qa.ui;

import com.sara.qa.base.BaseTest;
import com.sara.qa.pages.CartPage;
import com.sara.qa.pages.CheckoutPage;
import com.sara.qa.pages.HomePage;
import com.sara.qa.pages.OrderConfirmationPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("Flujo E2E de compra")
class CartFlowTests extends BaseTest {

    @Test
    @DisplayName("Compra completa: catalogo -> carrito -> checkout -> confirmacion")
    void fullPurchaseJourney() {
        HomePage home = new HomePage(driver()).open();

        home.addToCart("Teclado RGB blanco");
        home.addToCart("Monitor 24\" Full HD");

        CartPage cart = home.openCart();
        assertEquals(2, cart.lineCount());
        assertTrue(cart.lineNames().containsAll(
                List.of("Teclado RGB blanco", "Monitor 24\" Full HD")));
        // 39,90 + 149,00
        assertEquals("188,90 €", cart.total());

        CheckoutPage checkout = cart.checkout();
        checkout.fill("Ana Ejemplo", "ana@demo.dev", "600000000",
                "Calle Mayor 1", "Madrid", "28001");

        OrderConfirmationPage confirmation = checkout.submit();
        assertTrue(confirmation.orderNumber().startsWith("ORD-"));
        assertEquals("188,90 €", confirmation.total());
        assertTrue(confirmation.lineNames().contains("Teclado RGB blanco"));

        // La navegacion vuelve a estar limpia: volvemos al catalogo
        String url = confirmation.currentUrl();
        assertTrue(url.contains("/orders/"));
    }

    @Test
    @DisplayName("Quitar un producto recalcula el total")
    void removeItemUpdatesTotal() {
        HomePage home = new HomePage(driver()).open();
        home.addToCart("Ratón inalámbrico");
        home.addToCart("Webcam 1080p");

        CartPage cart = home.openCart();
        // 24,50 + 39,90
        assertEquals("64,40 €", cart.total());

        cart.removeItem("Webcam 1080p");

        assertEquals(1, cart.lineCount());
        assertEquals("24,50 €", cart.total());
    }

    @Test
    @DisplayName("Desde el carrito con productos se puede volver al catalogo")
    void backButtonReturnsToStore() {
        HomePage home = new HomePage(driver()).open();
        home.addToCart("Ratón inalámbrico");

        CartPage cart = home.openCart();
        assertEquals(1, cart.lineCount());

        home = cart.backToStore();
        assertTrue(home.currentUrl().endsWith("/"));
        assertEquals(6, home.productCount());
    }
}