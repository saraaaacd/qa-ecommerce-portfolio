package com.jesus.store.cart;

public record AddCartItemRequest(Long productId, Integer quantity) {

    public int quantityOrDefault() {
        return quantity == null ? 1 : quantity;
    }
}