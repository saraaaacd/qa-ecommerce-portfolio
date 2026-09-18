package com.jesus.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutPage extends BasePage {

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage fill(String name, String email, String phone,
                             String address, String city, String zip) {
        type(testId("customer-name"), name);
        type(testId("email"), email);
        if (phone != null && !phone.isBlank()) {
            type(testId("phone"), phone);
        }
        type(testId("address"), address);
        type(testId("city"), city);
        type(testId("zip"), zip);
        return this;
    }

    public OrderConfirmationPage submit() {
        click(testId("submit-order"));
        return new OrderConfirmationPage(driver);
    }

    public boolean hasFieldError(String fieldTestId) {
        By locator = By.cssSelector("[data-testid='" + fieldTestId + "']");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}