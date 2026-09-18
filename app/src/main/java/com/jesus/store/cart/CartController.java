package com.jesus.store.cart;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CartController {

    private final CartService cart;

    public CartController(CartService cart) {
        this.cart = cart;
    }

    @PostMapping("/cart/items/add/{productId}")
    public String add(@PathVariable Long productId) {
        cart.add(productId, 1);
        return "redirect:/?added=" + productId;
    }

    @PostMapping("/cart/items/remove/{productId}")
    public String remove(@PathVariable Long productId) {
        cart.remove(productId);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String view(Model model) {
        model.addAttribute("cart", cart.cart());
        return "cart";
    }
}