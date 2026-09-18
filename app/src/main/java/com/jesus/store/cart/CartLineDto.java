package com.jesus.store.cart;

import java.math.BigDecimal;

public record CartLineDto(Long productId, String sku, String name, BigDecimal unitPrice,
                          int quantity, BigDecimal total) {

    public static CartLineDto from(CartLine line) {
        return new CartLineDto(
                line.getProductId(),
                line.getSku(),
                line.getName(),
                line.getUnitPrice(),
                line.getQuantity(),
                line.total());
    }
}