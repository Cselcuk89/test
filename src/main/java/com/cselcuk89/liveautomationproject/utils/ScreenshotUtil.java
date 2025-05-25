package com.cselcuk89.liveautomationproject.utils; // Adjust package if needed

import com.microsoft.playwright.Page;
import org.slf4j.Logger; // Using SLF4J for logging within this utility
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "reports/screenshots/";

    public static String takeScreenshot(Page page, String screenshotName) {
        File directory = new File(SCREENSHOT_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("Screenshots directory created at: {}", directory.getAbsolutePath());
            } else {
                log.error("Failed to create screenshots directory at: {}", directory.getAbsolutePath());
                // Fallback or decide how to handle this error
                // For now, if directory creation fails, screenshot attempt will likely also fail or save to current dir.
            }
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fullScreenshotName = screenshotName + "_" + timestamp + ".png";
        String screenshotPath = SCREENSHOT_DIR + fullScreenshotName;

        try {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(screenshotPath))
                    .setFullPage(true)); // Capture full page, can be false if only viewport is needed
            log.info("Screenshot saved to: {}", Paths.get(screenshotPath).toAbsolutePath().toString());
            return Paths.get(screenshotPath).toAbsolutePath().toString(); // Return absolute path
        } catch (Exception e) {
            log.error("Error taking screenshot '{}': {}", fullScreenshotName, e.getMessage(), e);
            return null; // Indicate failure
        }
    }

    // Optional: Method to capture screenshot as Base64
    public static String takeScreenshotAsBase64(Page page) {
        if (page == null) {
            log.error("Playwright Page object is null. Cannot take screenshot as Base64.");
            return null;
        }
        try {
            byte[] buffer = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            return java.util.Base64.getEncoder().encodeToString(buffer);
        } catch (Exception e) {
            log.error("Error taking screenshot as Base64: {}", e.getMessage(), e);
            return null;
        }
    }
}
