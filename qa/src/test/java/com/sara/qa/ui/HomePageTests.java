package com.sara.qa.ui;

import com.sara.qa.base.BaseTest;
import com.sara.qa.pages.CartPage;
import com.sara.qa.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("ui")
@DisplayName("Home / Catalogo")
class HomePageTests extends BaseTest {

    @Test
    @DisplayName("La home muestra los 6 productos semilla")
    void homeShowsSeededProducts() {
        HomePage home = new HomePage(driver()).open();

        assertEquals(6, home.productCount());
        assertTrue(home.productNames().contains("Monitor 24\" Full HD"));
    }

    @Test
    @DisplayName("Anadir un producto incrementa el contador del carrito")
    void addProductIncrementsCartCounter() {
        HomePage home = new HomePage(driver()).open();
        assertEquals("0", home.cartCount());

        home.addToCart("Silla ergonomica");
        assertEquals("1", home.cartCount());

        home.addToCart("Silla ergonomica");
        assertEquals("2", home.cartCount());
    }

    @Test
    @DisplayName("Cada producto del catalogo muestra una imagen con alt descriptivo")
    void everyProductShowsAnImage() {
        HomePage home = new HomePage(driver()).open();

        List<WebElement> images = home.productImages();
        assertEquals(6, images.size());
        for (WebElement img : images) {
            assertTrue(img.isDisplayed(), "La imagen deberia ser visible");
            assertTrue(img.getDomAttribute("src").contains("/images/products/"),
                    "La imagen deberia servirse desde la ruta estatica de productos");
            assertFalse(img.getDomAttribute("alt").isBlank(),
                    "La imagen necesita un alt descriptivo (accesibilidad)");
        }
    }

    @Test
    @DisplayName("El menu permite navegar al carrito")
    void navMenuOpensEmptyCart() {
        HomePage home = new HomePage(driver()).open();

        CartPage cart = home.openCart();
        assertTrue(cart.isEmptyState());
        assertTrue(cart.currentUrl().endsWith("/cart"));
    }
}