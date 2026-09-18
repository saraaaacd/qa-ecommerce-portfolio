package com.sara.store.order;

import com.sara.store.cart.CartService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Checkout en el flujo web: formulario + validacion server-side.
 * No hay pasarela de pago: el pedido simplemente se guarda.
 */
@Controller
public class CheckoutController {

    private final OrderService orderService;
    private final CartService cart;

    public CheckoutController(OrderService orderService, CartService cart) {
        this.orderService = orderService;
        this.cart = cart;
    }

    @GetMapping("/checkout")
    public String form(Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("createOrderRequest", new CreateOrderRequest());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String submit(@Valid @ModelAttribute("createOrderRequest") CreateOrderRequest request,
                         BindingResult bindingResult, Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        if (bindingResult.hasErrors()) {
            return "checkout";
        }
        Order order = orderService.create(request, cart.cart());
        cart.clear();
        return "redirect:/orders/" + order.getOrderNumber();
    }
}