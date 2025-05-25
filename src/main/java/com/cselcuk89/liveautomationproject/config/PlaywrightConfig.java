package com.cselcuk89.liveautomationproject.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaywrightConfig {

    private static final Logger log = LogManager.getLogger(PlaywrightConfig.class); // Added logger

    @Value("${playwright.browser.name:chromium}")
    private String browserName;

    @Value("${playwright.browser.headless:true}")
    private boolean headless;

    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        log.info("Creating Playwright instance.");
        Playwright playwrightInstance = Playwright.create();
        log.info("Playwright instance created successfully.");
        return playwrightInstance;
    }

    @Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {
        log.info("Launching {} browser (headless: {})...", browserName, headless);
        LaunchOptions launchOptions = new LaunchOptions().setHeadless(headless);
        Browser browserInstance; // Renamed to avoid conflict with class name in some contexts

        switch (browserName.toLowerCase()) {
            case "firefox":
                browserInstance = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browserInstance = playwright.webkit().launch(launchOptions);
                break;
            case "chromium":
            default:
                browserInstance = playwright.chromium().launch(launchOptions);
                break;
        }
        log.info("{} browser launched successfully.", browserName);
        return browserInstance;
    }
}
