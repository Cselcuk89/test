package com.cselcuk89.liveautomationproject.page;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;
import com.cselcuk89.liveautomationproject.page.HomePage;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import

@Component
public class LoginPage extends BasePage {

    private static final Logger log = LogManager.getLogger(LoginPage.class); // Added logger

    private String usernameInputSelector = "#username";
    private String passwordInputSelector = "#password";
    private String loginButtonSelector = "#loginButton";
    private String errorMessageSelector = ".error-message";

    public LoginPage(Page page) {
        super(page);
        log.debug("LoginPage initialized.");
    }

    public void enterUsername(String username) {
        log.info("Executing method: enterUsername with username: {}", username != null ? "[PROVIDED]" : "[NULL]");
        if (username != null) {
            log.debug("Filling username '{}' into selector '{}'", username, usernameInputSelector);
            fill(usernameInputSelector, username);
        } else {
            log.warn("Username provided to enterUsername is null.");
        }
    }

    public void enterPassword(String password) {
        log.info("Executing method: enterPassword with password: [PROTECTED]"); // Avoid logging password directly
        if (password != null) {
            log.debug("Filling password into selector '{}'", passwordInputSelector);
            fill(passwordInputSelector, password);
        } else {
            log.warn("Password provided to enterPassword is null.");
        }
    }

    public void clickLoginButton() {
        log.info("Executing method: clickLoginButton");
        log.debug("Clicking login button with selector '{}'", loginButtonSelector);
        click(loginButtonSelector);
    }

    public HomePage login(String username, String password) {
        log.info("Executing method: login with username: {}", username != null ? "[PROVIDED]" : "[NULL]");
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        log.debug("Login action performed, returning new HomePage instance.");
        return new HomePage(this.page);
    }

    public LoginPage loginExpectingFailure(String username, String password) {
        log.info("Executing method: loginExpectingFailure with username: {}", username != null ? "[PROVIDED]" : "[NULL]");
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        log.debug("Login action (expecting failure) performed, returning current LoginPage instance.");
        return this;
    }

    public String getErrorMessage() {
        log.info("Executing method: getErrorMessage");
        if (isVisible(errorMessageSelector)) {
            String message = textContent(errorMessageSelector);
            log.debug("Error message found: '{}'", message);
            return message;
        }
        log.warn("Error message selector '{}' not visible or not found.", errorMessageSelector);
        return "Error message selector not visible or not found.";
    }

    public String getPageTitle() {
        return page.title();
    }
}
