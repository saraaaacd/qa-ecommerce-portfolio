package com.sara.qa.pages;

import com.sara.qa.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_LINE = testId("cart-line");
    private static final By LINE_NAME = testId("line-name");
    private static final By CART_TOTAL = testId("cart-total");
    private static final By CHECKOUT = testId("checkout");
    private static final By CART_EMPTY = testId("cart-empty");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage open() {
        driver.get(Config.appBaseUrl() + "/cart");
        return this;
    }

    public List<String> lineNames() {
        return allVisible(CART_LINE).stream()
                .map(line -> line.findElement(LINE_NAME).getText())
                .toList();
    }

    public int lineCount() {
        return allVisible(CART_LINE).size();
    }

    public String total() {
        return text(CART_TOTAL);
    }

    public CartPage removeItem(String productName) {
        By remove = By.xpath(
                "//tr[@data-testid='cart-line']"
                        + "[.//td[@data-testid='line-name'][normalize-space()='"
                        + productName + "']]//button[@data-testid='remove-item']");
        WebElement button = clickable(remove);
        button.click();
        // El form hace redirect a /cart: esperamos a que desaparezca el boton
        wait.until(ExpectedConditions.invisibilityOfElementLocated(remove));
        return this;
    }

    public CheckoutPage checkout() {
        click(CHECKOUT);
        return new CheckoutPage(driver);
    }

    public boolean isEmptyState() {
        return isVisible(CART_EMPTY);
    }
}