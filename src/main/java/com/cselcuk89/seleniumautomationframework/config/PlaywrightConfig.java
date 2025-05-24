package com.cselcuk89.seleniumautomationframework.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaywrightConfig {

    @Value("${playwright.browser.name:chromium}")
    private String browserName;

    @Value("${playwright.browser.headless:true}")
    private boolean headless;

    @Bean(destroyMethod = "close") // Manages Playwright object lifecycle
    public Playwright playwright() {
        return Playwright.create();
    }

    @Bean(destroyMethod = "close") // Manages Browser object lifecycle
    public Browser browser(Playwright playwright) { // Inject the Playwright bean
        LaunchOptions launchOptions = new LaunchOptions().setHeadless(headless);
        Browser browser;

        switch (browserName.toLowerCase()) {
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            case "chromium":
            default:
                browser = playwright.chromium().launch(launchOptions);
                break;
        }
        return browser;
    }
}
