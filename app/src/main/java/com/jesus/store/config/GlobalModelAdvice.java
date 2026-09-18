package com.jesus.store.config;

import com.jesus.store.cart.CartService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone el contador del carrito (de la sesion) a todas las vistas.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final CartService cart;

    public GlobalModelAdvice(CartService cart) {
        this.cart = cart;
    }

    @ModelAttribute
    public void addCartInfo(Model model) {
        model.addAttribute("cartCount", cart.totalItems());
    }
}