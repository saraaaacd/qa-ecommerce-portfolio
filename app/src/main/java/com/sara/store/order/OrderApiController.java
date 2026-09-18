package com.sara.store.order;

import com.sara.store.cart.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;
    private final OrderRepository orders;
    private final CartService cart;

    public OrderApiController(OrderService orderService, OrderRepository orders, CartService cart) {
        this.orderService = orderService;
        this.orders = orders;
        this.cart = cart;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderSummaryDto> create(@Valid @RequestBody CreateOrderRequest request) {
        if (cart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito esta vacio");
        }
        Order order = orderService.create(request, cart.cart());
        cart.clear();
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderSummaryDto.from(order));
    }

    @GetMapping("/{orderNumber}")
    public OrderSummaryDto show(@PathVariable String orderNumber) {
        Order order = orders.findByOrderNumberWithLines(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido no encontrado: " + orderNumber));
        return OrderSummaryDto.from(order);
    }
}