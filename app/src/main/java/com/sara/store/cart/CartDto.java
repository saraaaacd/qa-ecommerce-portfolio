package com.sara.store.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<CartLineDto> items, int totalItems, BigDecimal total) {

    public static CartDto from(Cart cart) {
        List<CartLineDto> items = cart.lines().stream().map(CartLineDto::from).toList();
        return new CartDto(items, cart.totalItems(), cart.total());
    }
}