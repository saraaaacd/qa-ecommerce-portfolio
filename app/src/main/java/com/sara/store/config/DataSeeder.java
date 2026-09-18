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
                new Product("KB-001", "Teclado mecanico RGB",
                        "Teclado mecanico con retroiluminacion RGB y switches lineales.", new BigDecimal("79.90"), "Perifericos",
                        "/images/products/teclado-rgb.svg"),
                new Product("MO-002", "Raton inalambrico",
                        "Raton ergonomico inalambrico con sensor optico de alta precision.", new BigDecimal("24.50"), "Perifericos",
                        "/images/products/raton-inalambrico.svg"),
                new Product("MN-003", "Monitor 24\" Full HD",
                        "Monitor IPS de 24 pulgadas con resolucion Full HD y 75 Hz.", new BigDecimal("149.00"), "Monitores",
                        "/images/products/monitor-24.svg"),
                new Product("AU-004", "Auriculares Bluetooth",
                        "Auriculares over-ear con cancelacion de ruido y 30 horas de bateria.", new BigDecimal("59.90"), "Audio",
                        "/images/products/auriculares-bluetooth.svg"),
                new Product("WE-005", "Webcam 1080p",
                        "Webcam Full HD con microfono integrado y correccion de luz ambiente.", new BigDecimal("39.90"), "Accesorios",
                        "/images/products/webcam-1080p.svg"),
                new Product("CH-006", "Silla ergonomica",
                        "Silla de oficina ergonomica con soporte lumbar ajustable.", new BigDecimal("219.00"), "Mobiliario",
                        "/images/products/silla-ergonomica.svg")
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