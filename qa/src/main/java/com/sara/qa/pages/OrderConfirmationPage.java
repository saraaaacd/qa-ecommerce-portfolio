package com.sara.qa.pages;

import org.openqa.selenium.WebDriver;

import java.util.List;

public class OrderConfirmationPage extends BasePage {

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }

    public String orderNumber() {
        return text(testId("order-number"));
    }

    public String total() {
        return text(testId("order-total"));
    }

    public List<String> lineNames() {
        return allVisible(testId("order-line")).stream()
                .map(line -> line.findElement(testId("order-line-name")).getText())
                .toList();
    }
}