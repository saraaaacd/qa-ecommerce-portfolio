package com.jesus.qa.ui;

import com.jesus.qa.base.BaseTest;
import com.jesus.qa.pages.CartPage;
import com.jesus.qa.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("El menu permite navegar al carrito")
    void navMenuOpensEmptyCart() {
        HomePage home = new HomePage(driver()).open();

        CartPage cart = home.openCart();
        assertTrue(cart.isEmptyState());
        assertTrue(cart.currentUrl().endsWith("/cart"));
    }
}