package com.cselcuk89.liveautomationproject.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;

@Component
public class ExtentManager {

    private static final Logger log = LogManager.getLogger(ExtentManager.class); // Added logger

    private ExtentReports extent;
    private ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Value("${extent.report.path:./reports/extent-report.html}")
    private String reportPath;

    @Value("${extent.document.title:Automation Test Report}")
    private String documentTitle;
    
    @Value("${extent.report.name:Test Results}")
    private String reportName;

    @PostConstruct
    public void init() {
        log.info("ExtentReports initializing with report path: {}", reportPath); // Added log
        // Ensure the directory for the report exists
        File reportDir = new File(reportPath).getParentFile();
        if (reportDir != null && !reportDir.exists()) {
            if (reportDir.mkdirs()) {
                log.info("Report directory created at: {}", reportDir.getAbsolutePath());
            } else {
                log.error("Failed to create report directory at: {}", reportDir.getAbsolutePath());
            }
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle(documentTitle);
        sparkReporter.config().setReportName(reportName);
        sparkReporter.config().setTheme(Theme.STANDARD); // Or Theme.DARK

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Optional: Add system information
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        log.info("ExtentReports initialized and ExtentSparkReporter attached."); // Added log
    }

    public synchronized ExtentTest createTest(String testName, String description) {
        log.info("Creating ExtentTest: Name='{}', Description='{}'", testName, description); // Added log
        ExtentTest test = extent.createTest(testName, description);
        extentTest.set(test);
        return test;
    }

    public synchronized ExtentTest getCurrentTest() {
        return extentTest.get();
    }

    public synchronized void removeCurrentTest() {
        extentTest.remove();
    }

    // Convenience methods to log directly to the current test
    public void logPass(String details) {
        if (getCurrentTest() != null) {
            getCurrentTest().pass(details);
            log.debug("Logging test as PASS: {}", details);
        } else {
            log.warn("Attempted to log PASS but current ExtentTest is null. Details: {}", details);
        }
    }

    public void logFail(String details) {
        if (getCurrentTest() != null) {
            getCurrentTest().fail(details);
            log.debug("Logging test as FAIL: {}", details);
        } else {
            log.warn("Attempted to log FAIL but current ExtentTest is null. Details: {}", details);
        }
    }

    public void logFail(Throwable throwable) {
        if (getCurrentTest() != null) {
            getCurrentTest().fail(throwable);
            log.debug("Logging test as FAIL with throwable: {}", throwable != null ? throwable.getMessage() : "null throwable");
        } else {
            log.warn("Attempted to log FAIL with throwable but current ExtentTest is null.");
        }
    }

    public void logInfo(String details) {
        if (getCurrentTest() != null) {
            getCurrentTest().info(details);
            log.debug("Logging test INFO: {}", details);
        } else {
            log.warn("Attempted to log INFO but current ExtentTest is null. Details: {}", details);
        }
    }

    public void logError(String details) {
        if (getCurrentTest() != null) {
            getCurrentTest().error(details);
            log.debug("Logging test ERROR: {}", details);
        } else {
            log.warn("Attempted to log ERROR but current ExtentTest is null. Details: {}", details);
        }
    }

    public void logSkipped(String details) {
        if (getCurrentTest() != null) {
            getCurrentTest().skip(details);
            log.debug("Logging test as SKIPPED: {}", details);
        } else {
            log.warn("Attempted to log SKIPPED but current ExtentTest is null. Details: {}", details);
        }
    }

    public void addScreenShotToReport(String imagePath) {
        if (getCurrentTest() != null) {
            getCurrentTest().addScreenCaptureFromPath(imagePath);
            log.debug("Adding screenshot to report from path: {}", imagePath);
        } else {
            log.warn("Attempted to add screenshot but current ExtentTest is null. Image path: {}", imagePath);
        }
    }

    public void addScreenShotToReportBase64(String base64Image) {
        if (getCurrentTest() != null) {
            getCurrentTest().addScreenCaptureFromBase64String(base64Image);
            log.debug("Adding Base64 screenshot to report.");
        } else {
            log.warn("Attempted to add Base64 screenshot but current ExtentTest is null.");
        }
    }

    @PreDestroy
    public void flushReports() {
        if (extent != null) {
            log.info("Flushing ExtentReports."); // Added log
            extent.flush();
        } else {
            log.warn("Attempted to flush ExtentReports, but instance is null.");
        }
    }
}
