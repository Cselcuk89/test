package com.cselcuk89.seleniumautomationframework.page;

import com.microsoft.playwright.Page;
// import com.microsoft.playwright.Locator; // If using Locator objects directly
import org.springframework.stereotype.Component;

// import java.util.List; // Not directly needed if using Locator.count() or similar

@Component
public class SearchPage extends BasePage {

    // Selectors for elements based on previous @FindBy
    private String searchInputSelector = "#searchInput";         // Placeholder ID
    private String searchButtonSelector = "#searchButton";       // Placeholder ID
    private String searchResultsSelector = ".product-item";      // Placeholder CSS class for search results
    private String noResultsMessageSelector = ".no-results"; // Placeholder for a "no results" message

    // Constructor
    public SearchPage(Page page) {
        super(page); // Pass page to BasePage constructor
        // System.out.println("SearchPage Initialized with Playwright Page."); // Logging can be added via Log4j2
    }

    public void enterSearchTerm(String searchTerm) {
        if (searchTerm != null) {
            // page.locator(searchInputSelector).clear(); // Playwright fill usually clears first
            fill(searchInputSelector, searchTerm);
        } else {
            System.err.println("Search term provided to enterSearchTerm is null.");
        }
    }

    public void clickSearchButton() {
        click(searchButtonSelector);
    }

    public int getResultsCount() {
        // Ensure the page has loaded results before counting
        // page.waitForSelector(searchResultsSelector); // Optional: wait for results to appear
        return page.locator(searchResultsSelector).count();
    }

    public boolean isNoResultsMessageDisplayed() {
        // This method checks for a specific "no results found" message element
        return isVisible(noResultsMessageSelector);
    }

    /**
     * Clicks the first product in the search results.
     * Assumes that clicking a product navigates to a ProductPage.
     * This is a conceptual method; actual implementation might vary based on how results are structured.
     * @return A new ProductPage instance.
     */
    public ProductPage clickFirstProduct() {
        if (getResultsCount() > 0) {
            page.locator(searchResultsSelector).first().click();
            return new ProductPage(this.page);
        } else {
            System.err.println("No search results found to click the first product.");
            // Or throw an exception
            // throw new IllegalStateException("Cannot click first product, no results found.");
            // For now, returning null or a new ProductPage which might then show an error state
            return new ProductPage(this.page); // Or null, depending on desired error handling
        }
    }
}
