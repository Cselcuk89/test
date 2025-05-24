package com.cselcuk89.seleniumautomationframework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.cselcuk89.seleniumautomationframework.base.BaseTest;
import com.cselcuk89.seleniumautomationframework.utils.ExtentManager;
import com.cselcuk89.seleniumautomationframework.utils.ScreenshotUtil;
import com.microsoft.playwright.Page; // Import Playwright Page
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
// Removed java.io.IOException as MediaEntityBuilder.createScreenCaptureFromPath doesn't explicitly throw it here

@Component
public class TestListener implements ITestListener {

    @Autowired
    private ExtentManager extentManager;

    // WebDriver related comments and direct autowiring removed.

    @Override
    public void onStart(ITestContext context) {
        System.out.println("TestListener:onStart - Test Suite started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("TestListener:onFinish - Test Suite finished: " + context.getName());
        if (extentManager != null) {
            extentManager.flushReports();
        } else {
            System.err.println("TestListener:onFinish - ExtentManager is null, cannot flush reports.");
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription(); // Can be null if no description
        description = (description == null || description.isEmpty()) ? "No description" : description;
        System.out.println("TestListener:onTestStart - Starting test: " + testName);
        if (extentManager != null) {
            extentManager.createTest(testName, description);
        } else {
            System.err.println("TestListener:onTestStart - ExtentManager is null, cannot create test.");
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("TestListener:onTestSuccess - Test PASSED: " + testName);
        ExtentTest currentTest = ExtentManager.getCurrentTest();
        if (currentTest != null) {
            currentTest.log(Status.PASS, "Test Passed");
        } else {
            System.err.println("TestListener:onTestSuccess - ExtentTest for " + testName + " is null, cannot log status.");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("TestListener:onTestFailure - Test FAILED: " + testName);
        ExtentTest currentTest = ExtentManager.getCurrentTest();

        if (currentTest == null) {
            System.err.println("TestListener:onTestFailure - ExtentTest for " + testName + " is null, cannot log failure or screenshot.");
            return;
        }

        currentTest.log(Status.FAIL, "Test Failed: " + result.getThrowable());

        Object testInstance = result.getInstance();
        if (testInstance instanceof BaseTest) {
            Page page = ((BaseTest) testInstance).getPage(); // Get Playwright Page
            if (page != null) {
                // Using testName for screenshot, can be timestamped if preferred:
                // String screenshotName = testName + "_" + System.currentTimeMillis();
                String screenshotPath = ScreenshotUtil.takeScreenshot(page, testName + "_failure");
                if (screenshotPath != null) {
                    try {
                        // Make path relative for report if possible
                        String relativePath = new File(System.getProperty("user.dir")).toURI().relativize(new File(screenshotPath).toURI()).getPath();
                        currentTest.fail("Screenshot on failure:", MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
                        // currentTest.addScreenCaptureFromPath(relativePath, "Failure Screenshot"); // Alternative
                        System.out.println("TestListener: Screenshot attached to report for " + testName + ": " + relativePath);
                    } catch (Exception e) {
                        System.err.println("TestListener: Failed to attach screenshot to Extent Report for " + testName + ". Error: " + e.getMessage());
                    }
                } else {
                    System.err.println("TestListener: Screenshot path is null for " + testName + ", not attaching to report.");
                }
            } else {
                System.err.println("TestListener: Playwright Page object is null in BaseTest for " + testName + ". Cannot take screenshot.");
            }
        } else {
            System.err.println("TestListener: Test instance for " + testName + " is not an instance of BaseTest. Cannot retrieve Page for screenshot.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("TestListener:onTestSkipped - Test SKIPPED: " + testName);
        ExtentTest currentTest = ExtentManager.getCurrentTest();
        if (currentTest != null) {
            currentTest.log(Status.SKIP, "Test Skipped: " + result.getThrowable());
        } else {
            System.err.println("TestListener:onTestSkipped - ExtentTest for " + testName + " is null, cannot log status.");
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not commonly used, but can be implemented if needed
        System.out.println("TestListener:onTestFailedButWithinSuccessPercentage - Test FAILED but within success percentage: " + result.getMethod().getMethodName());
    }
}
