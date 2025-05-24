package com.cselcuk89.seleniumautomationframework.base;

import com.cselcuk89.seleniumautomationframework.SeleniumAutomationFrameworkApplication;
import com.cselcuk89.seleniumautomationframework.listeners.TestListener; // Added for @Listeners
import com.cselcuk89.seleniumautomationframework.utils.ExtentManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners; // Added for @Listeners

@SpringBootTest
@ContextConfiguration(classes = SeleniumAutomationFrameworkApplication.class)
@Listeners({TestListener.class}) // Added TestListener registration
public abstract class BaseTest {

    @Autowired
    protected Browser browser; // Autowired Spring-managed Browser instance from PlaywrightConfig

    protected BrowserContext context;
    protected Page page;

    @Autowired
    protected ExtentManager extentManager; // Still needed for report cleanup

    @Value("${base.url}")
    protected String baseUrl;

    @Value("${default.timeout:30000}") // Default timeout for Playwright page operations
    protected double defaultTimeout;

    // Removed browserName and headlessMode @Value fields, now handled by PlaywrightConfig

    @BeforeMethod
    public void setUp() {
        // Create a new browser context and page for each test for isolation.
        // Using NewContextOptions to set viewport size as a good practice.
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));
        page = context.newPage();
        page.setDefaultTimeout(defaultTimeout); // Set default timeout for operations on this page

        if (baseUrl != null && !baseUrl.isEmpty()) {
            page.navigate(baseUrl);
            System.out.println("BaseTest.setUp(): Navigated to base URL: " + baseUrl);
        } else {
            System.err.println("BaseTest.setUp(): Base URL is not configured or is empty. Cannot navigate.");
            // Consider throwing an exception if baseUrl is critical
            // throw new IllegalStateException("Base URL is not configured.");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        System.out.println("BaseTest.tearDown(): Page and Context closed.");

        // Clean up ExtentTest from ThreadLocal after each test method
        if (extentManager != null) {
            extentManager.removeCurrentTest();
            System.out.println("BaseTest.tearDown(): Cleared ExtentTest from ThreadLocal.");
        } else {
            System.err.println("BaseTest.tearDown(): ExtentManager is null, cannot clear ExtentTest from ThreadLocal.");
        }
    }

    /**
     * Provides the current Playwright Page object to subclasses and listeners.
     * @return The current Page object.
     */
    public Page getPage() {
        return this.page;
    }
}
