package com.sara.qa.api;

import com.sara.qa.base.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("api")
@DisplayName("Catálogo - API")
class CatalogApiTests extends BaseApiTest {

    @Test
    @DisplayName("GET /api/products devuelve el catálogo semilla (6 productos)")
    void catalogHasSeededProducts() {
        List<Map<String, Object>> products = client.getProducts()
                .then().statusCode(200)
                .extract().body().jsonPath().getList("$");

        assertEquals(6, products.size());

        List<String> names = products.stream().map(p -> p.get("name").toString()).toList();
        assertTrue(names.contains("Teclado RGB blanco"));
        assertTrue(names.contains("Ratón inalámbrico"));
        assertTrue(names.contains("Monitor 24\" Full HD"));

        // Todo producto tiene precio > 0, sku y categoria
        assertTrue(products.stream()
                .allMatch(p -> ((Number) p.get("price")).doubleValue() > 0));
        assertTrue(products.stream().allMatch(p -> p.get("sku") != null));
        assertTrue(products.stream().allMatch(p -> p.get("category") != null));
    }

    @Test
    @DisplayName("GET /api/products/{id} devuelve el producto")
    void productByIdReturnsExpectedProduct() {
        long id = client.productId("Teclado RGB blanco");

        Map<String, Object> product = client.getProduct(id)
                .then().statusCode(200)
                .extract().body().jsonPath().getMap("$");

        assertEquals("Teclado RGB blanco", product.get("name"));
        assertEquals("KB-001", product.get("sku"));
    }

    @Test
    @DisplayName("GET /api/products/{id} con id inexistente devuelve 404")
    void unknownProductReturns404() {
        client.getProduct(99999)
                .then().statusCode(404);
    }
}