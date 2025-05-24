package com.cselcuk89.seleniumautomationframework.page;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

@Component
public class ProductPage extends BasePage {

    // Selectors for elements based on previous @FindBy
    private String productNameHeaderSelector = "h1";             // Placeholder for product name/header
    private String addToCartButtonSelector = "#addToCartButton"; // Placeholder ID
    private String productPriceSelector = ".product-price";      // Placeholder CSS class

    // Constructor
    public ProductPage(Page page) {
        super(page); // Pass page to BasePage constructor
        // System.out.println("ProductPage Initialized with Playwright Page."); // Logging can be added via Log4j2
    }

    public String getProductName() {
        if (isVisible(productNameHeaderSelector)) {
            return textContent(productNameHeaderSelector);
        }
        return "Product name header not visible or not found.";
    }

    public String getProductPrice() {
        if (isVisible(productPriceSelector)) {
            return textContent(productPriceSelector);
        }
        return "Product price element not visible or not found.";
    }

    public void clickAddToCartButton() {
        if (isAddToCartButtonDisplayed()) {
            click(addToCartButtonSelector);
            // Potentially return a CartPage object or handle navigation/confirmation
            // For example: return new CartPage(this.page);
        } else {
            System.err.println("Add to Cart button is not displayed or not found on ProductPage.");
            // Or throw an exception
            // throw new IllegalStateException("Add to Cart button not visible on ProductPage.");
        }
    }

    public boolean isAddToCartButtonDisplayed() {
        return isVisible(addToCartButtonSelector);
    }
}
