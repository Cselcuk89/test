package com.cselcuk89.liveautomationproject.page; // Updated package

import com.microsoft.playwright.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BasePage {

    protected final Page page;

    @Value("${base.url}")
    protected String baseUrl;

    @Value("${default.timeout:30000}")
    protected double defaultTimeout;

    public BasePage(Page page) {
        this.page = page;
        // System.out.println("BasePage Initialized with Playwright Page. URL: " + baseUrl + ", Timeout: " + defaultTimeout);
    }

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

    public String getBaseUrl() {
        return baseUrl;
    }

    public double getDefaultTimeout() {
        return defaultTimeout;
    }
}
