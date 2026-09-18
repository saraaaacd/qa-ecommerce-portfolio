package com.jesus.store.config;

import com.jesus.store.order.OrderRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint SOLO para entorno de pruebas: devuelve el sistema a un estado
 * conocido (borra pedidos creados por tests anteriores). El catalogo no se
 * toca => los ids de producto semilla se mantienen estables.
 */
@RestController
@RequestMapping("/api/test")
public class TestDataController {

    private final OrderRepository orders;

    public TestDataController(OrderRepository orders) {
        this.orders = orders;
    }

    @PostMapping("/reset")
    public Map<String, String> reset() {
        orders.deleteAll();
        return Map.of("status", "ok");
    }
}