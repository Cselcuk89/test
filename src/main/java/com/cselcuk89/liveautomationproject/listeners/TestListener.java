package com.cselcuk89.liveautomationproject.listeners; // Adjust if needed

import com.cselcuk89.liveautomationproject.base.BaseTest; // Assuming BaseTest exists/will exist
import com.cselcuk89.liveautomationproject.reporting.ExtentManager;
import com.cselcuk89.liveautomationproject.utils.ScreenshotUtil;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

@Component // Make it a Spring component to inject ExtentManager
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Autowired
    private ExtentManager extentManager; // Autowire Spring-managed ExtentManager

    @Override
    public void onStart(ITestContext context) {
        log.info("Test Suite started: {}", context.getName());
        // ExtentManager's @PostConstruct handles initialization, so no explicit call here needed
        // unless you want to add suite-level info to the report not covered by ExtentManager's init.
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Test Suite finished: {}", context.getName());
        // ExtentManager's @PreDestroy handles flushing, so no explicit call here needed
        // if Spring context lifecycle aligns with TestNG suite lifecycle.
        // However, for robust flushing with TestNG, explicitly calling flush here is safer,
        // especially if not running tests via Spring Test runner that manages full context shutdown.
        if (extentManager != null) {
             extentManager.flushReports();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        description = (description == null || description.isEmpty()) ? "No description provided" : description;
        log.info("Test started: {} ({})", testName, description);
        extentManager.createTest(testName, description);
        // Log test parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            StringBuilder params = new StringBuilder();
            for (Object param : parameters) {
                params.append(param == null ? "null" : param.toString()).append(", ");
            }
            if (params.length() > 2) { // Remove trailing comma and space
                 extentManager.logInfo("Parameters: [" + params.substring(0, params.length() - 2) + "]");
            } else {
                 extentManager.logInfo("Parameters: []");
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test PASSED: {}", result.getMethod().getMethodName());
        extentManager.logPass("Test passed");
        extentManager.removeCurrentTest(); // Clean up ThreadLocal
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("Test FAILED: {}", testName, result.getThrowable());
        extentManager.logFail(result.getThrowable()); // Log with stack trace

        // Capture screenshot
        Object testClassInstance = result.getInstance();
        if (testClassInstance instanceof BaseTest) { // Ensure test class extends your BaseTest
            BaseTest baseTest = (BaseTest) testClassInstance;
            Page page = baseTest.getPage(); // Assumes BaseTest has getPage() for Playwright
            if (page != null) {
                String screenshotPath = ScreenshotUtil.takeScreenshot(page, testName);
                if (screenshotPath != null) {
                    extentManager.addScreenShotToReport(screenshotPath);
                    log.info("Screenshot attached to report: {}", screenshotPath);
                } else {
                    log.error("Failed to capture or attach screenshot for test: {}", testName);
                }
            } else {
                log.warn("Playwright Page object was null in BaseTest for test: {}. Skipping screenshot.", testName);
            }
        } else {
            log.warn("Test class {} does not extend BaseTest. Skipping screenshot.", result.getTestClass().getName());
        }
        extentManager.removeCurrentTest(); // Clean up ThreadLocal
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Test SKIPPED: {}", result.getMethod().getMethodName());
        extentManager.logSkipped("Test skipped");
        if (result.getThrowable() != null) {
            extentManager.logInfo("Skip reason: " + result.getThrowable().getMessage());
        }
        extentManager.removeCurrentTest(); // Clean up ThreadLocal
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not commonly used, but log if it happens
        log.warn("Test FAILED within success percentage: {}", result.getMethod().getMethodName());
        onTestFailure(result); // Treat it as a failure for reporting
    }
}
