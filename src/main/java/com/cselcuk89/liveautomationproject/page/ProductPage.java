package com.cselcuk89.liveautomationproject.page;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import

@Component
public class ProductPage extends BasePage {

    private static final Logger log = LogManager.getLogger(ProductPage.class); // Added logger

    private String productNameHeaderSelector = "h1";
    private String addToCartButtonSelector = "#addToCartButton";
    private String productPriceSelector = ".product-price";

    public ProductPage(Page page) {
        super(page);
        log.debug("ProductPage initialized.");
    }

    public String getProductName() {
        log.info("Executing method: getProductName");
        if (isVisible(productNameHeaderSelector)) {
            String name = textContent(productNameHeaderSelector);
            log.debug("Product name found: '{}'", name);
            return name;
        }
        log.warn("Product name header selector '{}' not visible or not found.", productNameHeaderSelector);
        return "Product name header not visible or not found.";
    }

    public String getProductPrice() {
        log.info("Executing method: getProductPrice");
        if (isVisible(productPriceSelector)) {
            String price = textContent(productPriceSelector);
            log.debug("Product price found: '{}'", price);
            return price;
        }
        log.warn("Product price selector '{}' not visible or not found.", productPriceSelector);
        return "Product price element not visible or not found.";
    }

    public void clickAddToCartButton() {
        log.info("Executing method: clickAddToCartButton");
        if (isAddToCartButtonDisplayed()) {
            log.debug("Clicking 'Add to Cart' button with selector '{}'", addToCartButtonSelector);
            click(addToCartButtonSelector);
        } else {
            log.warn("Add to Cart button is not displayed or not found on ProductPage. Cannot click.");
        }
    }

    public boolean isAddToCartButtonDisplayed() {
        log.info("Executing method: isAddToCartButtonDisplayed");
        boolean visible = isVisible(addToCartButtonSelector);
        log.debug("'Add to Cart' button visibility: {}", visible);
        return visible;
    }
}
