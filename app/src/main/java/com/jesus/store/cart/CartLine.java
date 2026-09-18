package com.jesus.store.cart;

import com.jesus.store.catalog.Product;

import java.math.BigDecimal;

public class CartLine {

    private final Long productId;
    private final String sku;
    private final String name;
    private final BigDecimal unitPrice;
    private int quantity;

    public CartLine(Product product, int quantity) {
        this.productId = product.getId();
        this.sku = product.getSku();
        this.name = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public BigDecimal total() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}