package com.sara.store.cart;

import java.math.BigDecimal;

public record CartLineDto(Long productId, String sku, String name, BigDecimal unitPrice,
                          String imagePath, int quantity, BigDecimal total) {

    public static CartLineDto from(CartLine line) {
        return new CartLineDto(
                line.getProductId(),
                line.getSku(),
                line.getName(),
                line.getUnitPrice(),
                line.getImagePath(),
                line.getQuantity(),
                line.total());
    }
}