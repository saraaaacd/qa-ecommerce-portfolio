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
@DisplayName("Pedidos - API")
class OrderApiTests extends BaseApiTest {

    private static final String PRODUCTO = "Auriculares Bluetooth";

    @Test
    @DisplayName("Crear pedido con carrito devuelve numero y total")
    void createOrderFromCart() {
        BigDecimal unit = client.price(PRODUCTO);
        long id = client.productId(PRODUCTO);
        client.addToCart(id, 1).then().statusCode(201);

        JsonPath created = client.createOrder("Ana Ejemplo", "ana@demo.dev", "",
                        "Calle Mayor 1", "Madrid", "28001")
                .then().statusCode(201)
                .extract().body().jsonPath();

        String orderNumber = created.getString("orderNumber");
        assertTrue(orderNumber.startsWith("ORD-"));
        assertEquals("Ana Ejemplo", created.getString("customerName"));
        assertEquals(0, unit.compareTo(created.getObject("totalAmount", BigDecimal.class)));

        // El pedido se puede recuperar por numero
        JsonPath fetched = client.getOrder(orderNumber)
                .then().statusCode(200)
                .extract().body().jsonPath();
        assertEquals(orderNumber, fetched.getString("orderNumber"));
        assertEquals(1, fetched.getList("lines").size());

        // Tras el pedido el carrito queda vacio
        int totalItems = client.getCart().then().statusCode(200).extract().body().jsonPath().getInt("totalItems");
        assertEquals(0, totalItems);
    }

    @Test
    @DisplayName("Crear pedido con carrito vacio devuelve 400")
    void emptyCartReturns400() {
        client.createOrder("Ana", "ana@demo.dev", "", "Calle 1", "Madrid", "28001")
                .then().statusCode(400);
    }

    @Test
    @DisplayName("Campos obligatorios faltantes devuelven 400")
    void missingRequiredFieldsReturns400() {
        long id = client.productId(PRODUCTO);
        client.addToCart(id, 1).then().statusCode(201);

        // Sin nombre ni direccion ni ciudad
        client.createOrder("", "ana@demo.dev", "", "", "", "")
                .then().statusCode(400);
    }

    @Test
    @DisplayName("Email en formato invalido devuelve 400")
    void invalidEmailReturns400() {
        long id = client.productId(PRODUCTO);
        client.addToCart(id, 1).then().statusCode(201);

        client.createOrder("Ana Ejemplo", "esto-no-es-un-email", "",
                        "Calle Mayor 1", "Madrid", "28001")
                .then().statusCode(400);
    }

    @Test
    @DisplayName("Pedido inexistente devuelve 404")
    void unknownOrderReturns404() {
        client.getOrder("ORD-00000000-00000").then().statusCode(404);
    }

    @Test
    @DisplayName("Reset de datos de prueba borra pedidos anteriores")
    void resetClearsOrders() {
        long id = client.productId(PRODUCTO);
        client.addToCart(id, 1).then().statusCode(201);
        String orderNumber = client.createOrderAndReturnNumber(
                "Ana Ejemplo", "ana@demo.dev", "Calle Mayor 1", "Madrid", "28001");

        client.getOrder(orderNumber).then().statusCode(200);
        client.reset().then().statusCode(200);
        client.getOrder(orderNumber).then().statusCode(404);
    }
}