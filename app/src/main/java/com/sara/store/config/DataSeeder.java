package com.sara.store.config;

import com.sara.store.catalog.Product;
import com.sara.store.catalog.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pobla el catálogo con datos deterministas al arrancar.
 * Clave de QA: precios conocidos y estables => los tests pueden validar
 * totales exactos sin depender de datos variables.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ProductRepository products;

    public DataSeeder(ProductRepository products) {
        this.products = products;
    }

    @Override
    public void run(String... args) {
        List<Product> catalog = List.of(
                new Product("KB-001", "Teclado RGB blanco",
                        "Teclado retroiluminación RGB y switches lineales.", new BigDecimal("39.90"), "Periféricos",
                        "/images/products/teclado-rgb.jpg"),
                new Product("MO-002", "Ratón inalámbrico",
                        "Ratón inalámbrico con sensor óptico de alta precisión.", new BigDecimal("24.50"), "Periféricos",
                        "/images/products/raton-inalambrico.jpg"),
                new Product("MN-003", "Monitor 24\" Full HD",
                        "Monitor IPS de 24 pulgadas con resolución Full HD.", new BigDecimal("149.00"), "Monitores",
                        "/images/products/monitor.jpg"),
                new Product("AU-004", "Auriculares Bluetooth",
                        "Auriculares inalámbricos con cancelación de ruido y 30 horas de batería.", new BigDecimal("29.90"), "Audio",
                        "/images/products/auriculares-bluetooth.jpg"),
                new Product("WE-005", "Webcam 1080p",
                        "Webcam Full HD con micrófono integrado.", new BigDecimal("39.90"), "Accesorios",
                        "/images/products/webcam.png"),
                new Product("CH-006", "Silla ergonómica",
                        "Silla de oficina ergonómica con soporte lumbar ajustable.", new BigDecimal("167.00"), "Mobiliario",
                        "/images/products/silla-ergonómica.jpg")
        );

        int nuevos = 0;
        for (Product p : catalog) {
            if (products.findBySku(p.getSku()).isEmpty()) {
                products.save(p);
                nuevos++;
            }
        }
        log.info("Catalogo semilla: {} productos ({} insertados)", catalog.size(), nuevos);
    }
}