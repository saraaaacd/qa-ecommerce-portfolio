package com.jesus.store.catalog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductRepository products;

    public HomeController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", products.findAllByOrderByNameAsc());
        return "index";
    }
}