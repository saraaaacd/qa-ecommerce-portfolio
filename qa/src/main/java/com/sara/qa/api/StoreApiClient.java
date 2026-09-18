package com.sara.qa.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente REST del SUT. Cada instancia tiene su propio cookie jar
 * (sesion HTTP independiente) => el carrito nunca se mezcla entre tests.
 * <p>
 * El jar se gestiona de forma EXPLICITA: no todos los clientes HTTP
 * reenvian automaticamente la cookie JSESSIONID del servidor, asi que
 * capturamos el Set-Cookie de cada respuesta y lo adjuntamos a la siguiente.
 */
public class StoreApiClient {

    private final String baseUrl;
    private final RestAssured rest;
    private final Map<String, String> cookieJar = new HashMap<>();

    public StoreApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.rest = new RestAssured();
        this.rest.baseURI = baseUrl;
    }

    public String baseUrl() {
        return baseUrl;
    }

    private RequestSpecification spec() {
        RequestSpecification request = rest.given()
                .contentType("application/json")
                .accept("application/json");
        cookieJar.forEach(request::cookie);
        return request;
    }

    private Response send(Response response) {
        Map<String, String> newCookies = response.getCookies();
        if (!newCookies.isEmpty()) {
            cookieJar.putAll(newCookies);
        }
        return response;
    }

    // ---------- Catalog ----------

    public Response getProducts() {
        return send(spec().get("/api/products"));
    }

    public Response getProduct(long id) {
        return send(spec().get("/api/products/{id}", id));
    }

    public long productId(String name) {
        return productList().stream()
                .filter(p -> name.equals(p.get("name").toString()))
                .map(p -> ((Number) p.get("id")).longValue())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Producto no encontrado en catalogo: " + name));
    }

    public BigDecimal price(String name) {
        return productList().stream()
                .filter(p -> name.equals(p.get("name").toString()))
                .map(p -> new BigDecimal(p.get("price").toString()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Producto no encontrado en catalogo: " + name));
    }

    private List<Map<String, Object>> productList() {
        return getProducts().then().statusCode(200).extract().body().jsonPath().getList("$");
    }

    // ---------- Cart ----------

    public Response getCart() {
        return send(spec().get("/api/cart"));
    }

    public Response addToCart(long productId, int quantity) {
        String body = "{\"productId\":" + productId + ",\"quantity\":" + quantity + "}";
        return send(spec().body(body).post("/api/cart/items"));
    }

    public Response removeFromCart(long productId) {
        return send(spec().delete("/api/cart/items/{id}", productId));
    }

    public Response clearCart() {
        return send(spec().delete("/api/cart"));
    }

    // ---------- Orders ----------

    public Response createOrder(String name, String email, String phone,
                                String address, String city, String zip) {
        String body = "{"
                + "\"customerName\":\"" + escape(name) + "\","
                + "\"email\":\"" + escape(email) + "\","
                + "\"phone\":\"" + escape(phone) + "\","
                + "\"address\":\"" + escape(address) + "\","
                + "\"city\":\"" + escape(city) + "\","
                + "\"zip\":\"" + escape(zip) + "\""
                + "}";
        return send(spec().body(body).post("/api/orders"));
    }

    public Response getOrder(String orderNumber) {
        return send(spec().get("/api/orders/{number}", orderNumber));
    }

    // ---------- Test helpers ----------

    public Response reset() {
        return send(spec().post("/api/test/reset"));
    }

    public String createOrderAndReturnNumber(String name, String email,
                                              String address, String city, String zip) {
        return createOrder(name, email, "", address, city, zip)
                .then().statusCode(201).extract().body().jsonPath().getString("orderNumber");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}