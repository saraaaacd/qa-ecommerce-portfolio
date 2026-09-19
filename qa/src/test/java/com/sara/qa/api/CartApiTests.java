package com.sara.qa.api;

import com.sara.qa.base.BaseApiTest;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("api")
@DisplayName("Carrito - API")
class CartApiTests extends BaseApiTest {

    private static final String PRODUCTO = "Teclado RGB blanco";

    @Test
    @DisplayName("Anadir 2 unidades calcula totalItems y total")
    void addTwoUnitsComputesTotals() {
        BigDecimal unit = client.price(PRODUCTO);
        long id = client.productId(PRODUCTO);

        JsonPath cart = client.addToCart(id, 2)
                .then().statusCode(201)
                .extract().body().jsonPath();

        assertEquals(2, cart.getInt("totalItems"));
        assertEquals(2, cart.getInt("items[0].quantity"));
        assertEquals(0, unit.multiply(BigDecimal.valueOf(2)).compareTo(cart.getObject("total", BigDecimal.class)));
        assertEquals(0, unit.multiply(BigDecimal.valueOf(2)).compareTo(cart.getObject("items[0].total", BigDecimal.class)));
    }

    @Test
    @DisplayName("Anadir productos distintos suma sus importes")
    void addDifferentProductsSumsTotals() {
        BigDecimal teclado = client.price("Teclado RGB blanco");
        BigDecimal monitor = client.price("Monitor 24\" Full HD");

        long tecladoId = client.productId("Teclado RGB blanco");
        long monitorId = client.productId("Monitor 24\" Full HD");

        client.addToCart(tecladoId, 1).then().statusCode(201);
        client.addToCart(monitorId, 1).then().statusCode(201);

        JsonPath cart = client.getCart().then().statusCode(200).extract().body().jsonPath();

        assertEquals(2, cart.getInt("totalItems"));
        assertEquals(0, teclado.add(monitor).compareTo(cart.getObject("total", BigDecimal.class)));
    }

    @Test
    @DisplayName("Quitar un producto actualiza el carrito")
    void removeItemUpdatesCart() {
        long tecladoId = client.productId("Teclado RGB blanco");
        long monitorId = client.productId("Monitor 24\" Full HD");

        client.addToCart(tecladoId, 1).then().statusCode(201);
        client.addToCart(monitorId, 1).then().statusCode(201);
        client.removeFromCart(tecladoId).then().statusCode(200);

        JsonPath cart = client.getCart().then().statusCode(200).extract().body().jsonPath();
        assertEquals(1, cart.getInt("totalItems"));
        assertEquals(1, cart.getList("items").size());
    }

    @Test
    @DisplayName("Vaciar el carrito deja 0 items")
    void clearCartEmptiesCart() {
        long id = client.productId(PRODUCTO);
        client.addToCart(id, 1).then().statusCode(201);

        JsonPath cart = client.clearCart().then().statusCode(200).extract().body().jsonPath();

        assertEquals(0, cart.getInt("totalItems"));
        assertTrue(cart.getList("items").isEmpty());
    }

    @Test
    @DisplayName("Anadir un producto inexistente devuelve 404")
    void unknownProductReturns404() {
        client.addToCart(99999, 1).then().statusCode(404);
    }

    @Test
    @DisplayName("El carrito esta aislado por sesion")
    void cartIsIsolatedBetweenSessions() {
        long id = client.productId(PRODUCTO);

        StoreApiClient otroCliente = new StoreApiClient(urlBase());

        client.addToCart(id, 3).then().statusCode(201);
        // La sesion de 'otroCliente' NO ve el carrito de 'client'
        JsonPath carritoAjeno = otroCliente.getCart().then().statusCode(200).extract().body().jsonPath();
        assertEquals(0, carritoAjeno.getInt("totalItems"));

        assertEquals(3, client.getCart().then().statusCode(200).extract().body().jsonPath().getInt("totalItems"));
    }

    private String urlBase() {
        return client.baseUrl();
    }
}