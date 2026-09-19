package com.sara.qa.ui;

import com.sara.qa.base.BaseTest;
import com.sara.qa.config.Config;
import com.sara.qa.pages.CartPage;
import com.sara.qa.pages.CheckoutPage;
import com.sara.qa.pages.HomePage;
import com.sara.qa.pages.OrderConfirmationPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@DisplayName("Checkout - validacion del formulario")
class CheckoutUiTests extends BaseTest {

    private CheckoutPage checkoutConProductoEnCarrito() {
        HomePage home = new HomePage(driver()).open();
        home.addToCart("Silla ergonómica");
        CartPage cart = home.openCart();
        return cart.checkout();
    }

    @Test
    @DisplayName("Enviar sin nombre muestra error de validacion")
    void missingNameShowsError() {
        CheckoutPage checkout = checkoutConProductoEnCarrito();
        checkout.fill("", "ana@demo.dev", "", "Calle Mayor 1", "Madrid", "28001");
        checkout.submit();

        assertTrue(checkout.hasFieldError("customer-name-error"));
        assertTrue(checkout.currentUrl().contains("/checkout"));
    }

    @Test
    @DisplayName("Todos los campos obligatorios en blanco muestran todos los errores")
    void allRequiredFieldsBlankShowsAllErrors() {
        CheckoutPage checkout = checkoutConProductoEnCarrito();
        checkout.fill("", "", "", "", "", "");
        checkout.submit();

        assertTrue(checkout.hasFieldError("customer-name-error"));
        assertTrue(checkout.hasFieldError("address-error"));
        assertTrue(checkout.hasFieldError("city-error"));
        assertTrue(checkout.hasFieldError("zip-error"));
        // El email es opcional: en blanco no muestra error
        assertFalse(checkout.hasFieldError("email-error"));
        assertTrue(checkout.currentUrl().contains("/checkout"));

        // El pedido no se ha creado: el carrito sigue conservando el articulo
        driver().get(Config.appBaseUrl() + "/cart");
        assertFalse(new CartPage(driver()).isEmptyState());
    }

    @Test
    @DisplayName("Campos obligatorios solo con espacios muestran error de validacion")
    void whitespaceOnlyRequiredFieldsShowsErrors() {
        CheckoutPage checkout = checkoutConProductoEnCarrito();
        checkout.fill("   ", "ana@demo.dev", "", "   ", "   ", "   ");
        checkout.submit();

        assertTrue(checkout.hasFieldError("customer-name-error"));
        assertTrue(checkout.hasFieldError("address-error"));
        assertTrue(checkout.hasFieldError("city-error"));
        assertTrue(checkout.hasFieldError("zip-error"));
        assertTrue(checkout.currentUrl().contains("/checkout"));
    }

    @Test
    @DisplayName("Email en blanco es valido porque el campo es opcional")
    void blankEmailIsAccepted() {
        CheckoutPage checkout = checkoutConProductoEnCarrito();
        checkout.fill("Ana Ejemplo", "", "", "Calle Mayor 1", "Madrid", "28001");
        OrderConfirmationPage confirmation = checkout.submit();

        assertTrue(confirmation.currentUrl().contains("/orders/"));
        assertTrue(confirmation.orderNumber().startsWith("ORD-"));
    }

    @Test
    @DisplayName("Email en formato invalido muestra error de validacion")
    void invalidEmailShowsError() {
        CheckoutPage checkout = checkoutConProductoEnCarrito();
        checkout.fill("Ana Ejemplo", "esto-no-es-un-email", "", "Calle Mayor 1", "Madrid", "28001");
        checkout.submit();

        assertTrue(checkout.hasFieldError("email-error"));
        assertTrue(checkout.hasFieldError("email-format-hint"));
    }

    @Test
    @DisplayName("Emails incompletos como ana@. son rechazados")
    void incompleteEmailIsRejected() {
        CheckoutPage checkout = checkoutConProductoEnCarrito();
        checkout.fill("Ana Ejemplo", "ana@.", "", "Calle Mayor 1", "Madrid", "28001");
        checkout.submit();

        assertTrue(checkout.hasFieldError("email-error"));
        assertTrue(checkout.hasFieldError("email-format-hint"));
    }

    @Test
    @DisplayName("Sin carrito, el checkout redirige a la pagina del carrito")
    void emptyCartRedirectsToCart() {
        // El boton de checkout solo se renderiza con articulos en el carrito,
        // asi que accedemos a /checkout directamente.
        driver().get(Config.appBaseUrl() + "/checkout");

        CartPage cart = new CartPage(driver());
        assertTrue(cart.currentUrl().contains("/cart"));
        assertTrue(cart.isEmptyState());
    }
}