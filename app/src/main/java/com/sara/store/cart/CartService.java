package com.sara.store.cart;

import com.sara.store.catalog.Product;
import com.sara.store.catalog.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * El carrito vive en la SESION HTTP (una instancia de este bean por sesion).
 * Decision de QA: aislar estado; cada navegador/cliente tiene su propio
 * carrito y los tests no se pisan entre si.
 */
@Service
@SessionScope
public class CartService {

    private final Cart cart = new Cart();
    private final ProductRepository products;

    public CartService(ProductRepository products) {
        this.products = products;
    }

    public void add(Long productId, int quantity) {
        if (quantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que 0");
        }
        Product product = products.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + productId));
        cart.add(product, quantity);
    }

    public void remove(Long productId) {
        cart.remove(productId);
    }

    public void clear() {
        cart.clear();
    }

    public Cart cart() {
        return cart;
    }

    public List<CartLine> lines() {
        return cart.lines();
    }

    public int totalItems() {
        return cart.totalItems();
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }
}