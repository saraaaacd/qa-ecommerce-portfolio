package com.sara.store.order;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class OrderViewController {

    private final OrderRepository orders;

    public OrderViewController(OrderRepository orders) {
        this.orders = orders;
    }

    @GetMapping("/orders/{orderNumber}")
    public String show(@PathVariable String orderNumber, Model model) {
        Order order = orders.findByOrderNumberWithLines(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido no encontrado: " + orderNumber));
        model.addAttribute("order", order);
        return "order-confirmation";
    }
}