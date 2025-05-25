package com.cselcuk89.liveautomationproject.base; // Updated package

import com.cselcuk89.liveautomationproject.SeleniumAutomationFrameworkApplication; // Updated import
import com.cselcuk89.liveautomationproject.listeners.TestListener; // Updated import
import com.cselcuk89.liveautomationproject.utils.ExtentManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@SpringBootTest
@ContextConfiguration(classes = SeleniumAutomationFrameworkApplication.class)
@Listeners({TestListener.class})
public abstract class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class); // Added logger

    @Autowired
    protected Browser browser;

    protected BrowserContext context;
    protected Page page;

    @Autowired
    protected ExtentManager extentManager;

    @Value("${base.url}")
    protected String baseUrl;

    @Value("${default.timeout:30000}")
    protected double defaultTimeout;

    @BeforeMethod
    public void setUp() {
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));
        page = context.newPage();
        page.setDefaultTimeout(defaultTimeout);

        log.info("Setting up test. Base URL: {}", baseUrl); // Added log
        if (baseUrl != null && !baseUrl.isEmpty()) {
            page.navigate(baseUrl);
            log.debug("Navigated to base URL: {}", baseUrl);
        } else {
            log.error("Base URL is not configured or is empty. Cannot navigate.");
        }
        log.debug("Browser context and page created. Default timeout: {}ms", defaultTimeout); // Added log
    }

    @AfterMethod
    public void tearDown() {
        log.info("Tearing down test."); // Added log
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        log.debug("Page and context closed."); // Added log

        if (extentManager != null) {
            extentManager.removeCurrentTest();
            log.debug("Cleared ExtentTest from ThreadLocal.");
        } else {
            log.warn("ExtentManager is null, cannot clear ExtentTest from ThreadLocal.");
        }
    }

    public Page getPage() {
        return this.page;
    }
}
