package com.cselcuk89.liveautomationproject.page;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;
import com.cselcuk89.liveautomationproject.page.ProductPage;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import

@Component
public class SearchPage extends BasePage {

    private static final Logger log = LogManager.getLogger(SearchPage.class); // Added logger

    private String searchInputSelector = "#searchInput";
    private String searchButtonSelector = "#searchButton";
    private String searchResultsSelector = ".product-item";
    private String noResultsMessageSelector = ".no-results";

    public SearchPage(Page page) {
        super(page);
        log.debug("SearchPage initialized.");
    }

    public void enterSearchTerm(String searchTerm) {
        log.info("Executing method: enterSearchTerm with term: {}", searchTerm != null ? searchTerm : "[NULL]");
        if (searchTerm != null) {
            log.debug("Filling search term '{}' into selector '{}'", searchTerm, searchInputSelector);
            fill(searchInputSelector, searchTerm);
        } else {
            log.warn("Search term provided to enterSearchTerm is null.");
        }
    }

    public void clickSearchButton() {
        log.info("Executing method: clickSearchButton");
        log.debug("Clicking search button with selector '{}'", searchButtonSelector);
        click(searchButtonSelector);
    }

    public int getResultsCount() {
        log.info("Executing method: getResultsCount");
        int count = page.locator(searchResultsSelector).count();
        log.debug("Number of search results found: {}", count);
        return count;
    }

    public boolean isNoResultsMessageDisplayed() {
        log.info("Executing method: isNoResultsMessageDisplayed");
        boolean visible = isVisible(noResultsMessageSelector);
        log.debug("No results message visibility: {}", visible);
        return visible;
    }

    public ProductPage clickFirstProduct() {
        log.info("Executing method: clickFirstProduct");
        if (getResultsCount() > 0) {
            log.debug("Clicking first product in results with selector '{}'", searchResultsSelector);
            page.locator(searchResultsSelector).first().click();
            log.debug("First product clicked, returning new ProductPage instance.");
            return new ProductPage(this.page);
        } else {
            log.warn("No search results found to click the first product.");
            // Returning a new ProductPage might be problematic if no product is actually loaded.
            // Consider throwing an exception or returning null based on expected behavior.
            return new ProductPage(this.page); // Or throw new IllegalStateException("No products to click.");
        }
    }
}
