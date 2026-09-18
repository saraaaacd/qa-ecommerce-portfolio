package com.sara.qa.pages;

import com.sara.qa.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HomePage extends BasePage {

    private static final By PRODUCT_CARD = testId("product-card");
    private static final By PRODUCT_NAME = testId("product-name");
    private static final By PRICE = testId("product-price");
    private static final By CART_COUNT = testId("cart-count");
    private static final By NAV_CART = testId("nav-cart");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        driver.get(Config.appBaseUrl());
        return this;
    }

    public List<String> productNames() {
        return allVisible(PRODUCT_CARD).stream()
                .map(card -> card.findElement(PRODUCT_NAME).getText())
                .toList();
    }

    public List<WebElement> productImages() {
        return allVisible(PRODUCT_CARD).stream()
                .map(card -> card.findElement(testId("product-image")))
                .toList();
    }

    public int productCount() {
        return allVisible(PRODUCT_CARD).size();
    }

    public HomePage addToCart(String productName) {
        WebElement card = allVisible(PRODUCT_CARD).stream()
                .filter(c -> c.findElement(PRODUCT_NAME).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Producto no visible en catalogo: " + productName));
        WebElement addButton = card.findElement(testId("add-to-cart"));
        wait.until(ExpectedConditions.elementToBeClickable(addButton));
        addButton.click();
        // El form hace redirect a /?added=N: esperamos a que la pagina vieja se
        // reemplace (staleness) para leer el estado ya persistido por el servidor.
        wait.until(ExpectedConditions.stalenessOf(addButton));
        return this;
    }

    public String cartCount() {
        return text(CART_COUNT);
    }

    public CartPage openCart() {
        click(NAV_CART);
        return new CartPage(driver);
    }
}