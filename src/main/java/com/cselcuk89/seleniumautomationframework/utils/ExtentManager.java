package com.cselcuk89.seleniumautomationframework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;

@Component
public class ExtentManager {

    private ExtentReports extentReports;

    @Value("${extent.report.path:./reports/extent-report.html}")
    private String reportPath;

    // ThreadLocal to hold ExtentTest instances for parallel test execution
    private static ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    @PostConstruct // Executed after dependency injection is done to perform any initialization
    public void initialize() {
        if (extentReports == null) {
            System.out.println("ExtentManager: Initializing ExtentReports.");
            System.out.println("ExtentManager: Report will be saved to: " + reportPath);

            // Ensure parent directory exists
            File reportFile = new File(reportPath);
            File parentDir = reportFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                System.out.println("ExtentManager: Creating report directory: " + parentDir.getAbsolutePath());
                if (!parentDir.mkdirs()) {
                    System.err.println("ExtentManager: Failed to create report directory: " + parentDir.getAbsolutePath());
                    // Fallback or throw error
                }
            }


            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Selenium Automation Report");
            sparkReporter.config().setReportName("Test Execution Report");
            sparkReporter.config().setEncoding("utf-8");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            // System information (optional)
            extentReports.setSystemInfo("Application Environment", "QA");
            extentReports.setSystemInfo("Browser", "Chrome/Firefox"); // This can be made dynamic
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            System.out.println("ExtentManager: ExtentReports initialized successfully.");
        }
    }

    public synchronized ExtentReports getExtentReports() {
        if (extentReports == null) {
            // This might happen if accessed before @PostConstruct or if initialize failed
            System.err.println("ExtentManager: getExtentReports() called but extentReports is null. Attempting re-initialization.");
            initialize(); // Attempt to initialize again, though this indicates an issue.
        }
        return extentReports;
    }

    public synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest test = getExtentReports().createTest(testName, description);
        extentTestThreadLocal.set(test);
        return test;
    }

    public synchronized static ExtentTest getCurrentTest() {
        return extentTestThreadLocal.get();
    }

    public synchronized void removeCurrentTest() {
        extentTestThreadLocal.remove();
    }

    @PreDestroy // Executed before the bean is destroyed
    public void flushReports() {
        if (extentReports != null) {
            System.out.println("ExtentManager: Flushing ExtentReports.");
            extentReports.flush();
            System.out.println("ExtentManager: ExtentReports flushed successfully.");
        } else {
            System.err.println("ExtentManager: flushReports() called but extentReports is null.");
        }
    }
}
