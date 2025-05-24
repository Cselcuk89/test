package com.cselcuk89.seleniumautomationframework.page;

import com.microsoft.playwright.Page;
// import com.microsoft.playwright.options.NavigateOptions; // If specific navigate options are needed
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component // Kept as per instruction, though concrete pages will pass Page object
public class BasePage {

    protected final Page page;

    @Value("${base.url}")
    protected String baseUrl;

    @Value("${default.timeout:30000}") // Default timeout in milliseconds for Playwright
    protected double defaultTimeout; // Playwright timeouts are double (milliseconds)

    // Constructor for concrete page objects to call
    public BasePage(Page page) {
        this.page = page;
        // Optional: Set default timeout for the page instance if needed.
        // However, timeouts are often managed at the BrowserContext level or per operation.
        // if (this.page != null) {
        //     this.page.setDefaultTimeout(this.defaultTimeout);
        // }
        // System.out.println("BasePage Initialized with Playwright Page. URL: " + baseUrl + ", Timeout: " + defaultTimeout);
    }

    // Navigation methods
    public void navigate(String url) {
        if (page != null) {
            page.navigate(url);
        } else {
            System.err.println("Playwright Page object is null in BasePage. Cannot navigate.");
        }
    }

    public void navigateToBaseUrl() {
        if (page != null && baseUrl != null && !baseUrl.isEmpty()) {
            page.navigate(baseUrl);
        } else {
            if (page == null) {
                System.err.println("Playwright Page object is null in BasePage. Cannot navigate to base URL.");
            }
            if (baseUrl == null || baseUrl.isEmpty()) {
                System.err.println("Base URL is not configured in BasePage. Cannot navigate.");
            }
        }
    }

    public String getCurrentUrl() {
        return page != null ? page.url() : "";
    }

    // Interaction methods
    public void click(String selector) {
        if (page != null) {
            page.click(selector);
        } else {
            System.err.println("Playwright Page object is null. Cannot click selector: " + selector);
        }
    }

    public void fill(String selector, String text) {
        if (page != null) {
            page.fill(selector, text);
        } else {
            System.err.println("Playwright Page object is null. Cannot fill selector: " + selector);
        }
    }

    public String textContent(String selector) {
        if (page != null) {
            return page.textContent(selector);
        }
        System.err.println("Playwright Page object is null. Cannot get text content for selector: " + selector);
        return null;
    }

    public boolean isVisible(String selector) {
        if (page != null) {
            return page.isVisible(selector);
        }
        System.err.println("Playwright Page object is null. Cannot check visibility for selector: " + selector);
        return false;
    }

    // Property accessors
    public String getBaseUrl() {
        return baseUrl;
    }

    public double getDefaultTimeout() {
        return defaultTimeout;
    }

    // Add other common Playwright actions as needed
    // e.g., page.locator(selector).hover(), page.locator(selector).isChecked(), etc.
    // public void hover(String selector) {
    //     if (page != null) page.locator(selector).hover();
    // }
    //
    // public String getAttribute(String selector, String attributeName) {
    //     if (page != null) return page.locator(selector).getAttribute(attributeName);
    //     return null;
    // }
}
