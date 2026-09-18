package com.jesus.store.cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cart;

    public CartApiController(CartService cart) {
        this.cart = cart;
    }

    @GetMapping
    public CartDto view() {
        return CartDto.from(cart.cart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(@RequestBody AddCartItemRequest request) {
        if (request.productId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo productId es obligatorio");
        }
        cart.add(request.productId(), request.quantityOrDefault());
        return ResponseEntity.status(HttpStatus.CREATED).body(CartDto.from(cart.cart()));
    }

    @DeleteMapping("/items/{productId}")
    public CartDto removeItem(@PathVariable Long productId) {
        cart.remove(productId);
        return CartDto.from(cart.cart());
    }

    @DeleteMapping
    public CartDto clear() {
        cart.clear();
        return CartDto.from(cart.cart());
    }
}