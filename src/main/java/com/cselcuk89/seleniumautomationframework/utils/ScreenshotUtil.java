package com.cselcuk89.seleniumautomationframework.utils;

import com.microsoft.playwright.Page; // Playwright import
import java.io.File;
import java.nio.file.Paths; // For Playwright path
import java.text.SimpleDateFormat;
import java.util.Date;
// Selenium and FileUtils imports are removed

public class ScreenshotUtil {

    // Base path for screenshots
    private static final String SCREENSHOT_BASE_PATH = "reports/screenshots/";

    /**
     * Takes a screenshot of the current page using Playwright.
     *
     * @param page           The Playwright Page object.
     * @param screenshotName A logical name for the screenshot (e.g., test method name).
     * @return The path to the saved screenshot file, or null if an error occurred.
     */
    public static String takeScreenshot(Page page, String screenshotName) {
        if (page == null) {
            System.err.println("ScreenshotUtil: Playwright Page object is null. Cannot take screenshot.");
            return null;
        }

        // Ensure screenshot directory exists
        File directory = new File(SCREENSHOT_BASE_PATH);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.err.println("ScreenshotUtil: Failed to create screenshot directory: " + directory.getAbsolutePath());
                return null;
            }
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        String filePath = SCREENSHOT_BASE_PATH + fileName;

        try {
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(filePath))
                .setFullPage(true)); // Capture the full scrollable page

            System.out.println("ScreenshotUtil: Screenshot saved to " + new File(filePath).getAbsolutePath());
            return new File(filePath).getAbsolutePath(); // Return absolute path
        } catch (Exception e) {
            // Playwright exceptions are typically runtime exceptions (PlaywrightException)
            System.err.println("ScreenshotUtil: Failed to save screenshot '" + fileName + "'. Error: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
            return null;
        }
    }
}
