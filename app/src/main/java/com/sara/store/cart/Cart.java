package com.sara.store.cart;

import com.sara.store.catalog.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart {

    private final Map<Long, CartLine> lines = new LinkedHashMap<>();

    public void add(Product product, int quantity) {
        CartLine line = lines.get(product.getId());
        if (line == null) {
            lines.put(product.getId(), new CartLine(product, quantity));
        } else {
            line.addQuantity(quantity);
        }
    }

    public void remove(Long productId) {
        lines.remove(productId);
    }

    public void clear() {
        lines.clear();
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public List<CartLine> lines() {
        return new ArrayList<>(lines.values());
    }

    public int totalItems() {
        return lines.values().stream().mapToInt(CartLine::getQuantity).sum();
    }

    public BigDecimal total() {
        return lines.values().stream()
                .map(CartLine::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}