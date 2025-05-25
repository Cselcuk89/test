package com.cselcuk89.liveautomationproject.page;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;
import com.cselcuk89.liveautomationproject.page.LoginPage;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import

@Component
public class HomePage extends BasePage {

    private static final Logger log = LogManager.getLogger(HomePage.class); // Added logger

    private String logoutButtonSelector = "#logoutButton"; 
    private String welcomeMessageHeaderSelector = "h1";

    public HomePage(Page page) {
        super(page);
        log.debug("HomePage initialized.");
    }

    public String getHomePageTitle() {
        log.info("Executing method: getHomePageTitle");
        String title = page.title();
        log.debug("Current page title: {}", title);
        return title;
    }

    public boolean isLogoutButtonDisplayed() {
        log.info("Executing method: isLogoutButtonDisplayed");
        boolean visible = isVisible(logoutButtonSelector);
        log.debug("Logout button visibility: {}", visible);
        return visible;
    }

    public String getWelcomeMessage() {
        log.info("Executing method: getWelcomeMessage");
        if (isVisible(welcomeMessageHeaderSelector)) {
            String message = textContent(welcomeMessageHeaderSelector);
            log.debug("Welcome message found: '{}'", message);
            return message;
        }
        log.warn("Welcome message header selector '{}' not visible or not found.", welcomeMessageHeaderSelector);
        return "Welcome message header not visible or not found.";
    }

    public LoginPage clickLogoutButton() {
        log.info("Executing method: clickLogoutButton");
        if (isLogoutButtonDisplayed()) {
            log.debug("Clicking logout button with selector '{}'", logoutButtonSelector);
            click(logoutButtonSelector);
            log.debug("Logout button clicked, returning new LoginPage instance.");
            return new LoginPage(this.page);
        } else {
            log.warn("Logout button is not displayed or not found on HomePage. Cannot click.");
            // Depending on desired behavior, might still return new LoginPage or throw error
            return new LoginPage(this.page); // Or handle error more gracefully
        }
    }
}
