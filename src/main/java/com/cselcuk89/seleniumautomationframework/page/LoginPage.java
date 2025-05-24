package com.cselcuk89.seleniumautomationframework.page;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

@Component
public class LoginPage extends BasePage {

    // Selectors for elements based on previous @FindBy IDs
    private String usernameInputSelector = "#username";
    private String passwordInputSelector = "#password";
    private String loginButtonSelector = "#loginButton";
    // Assuming an error message element might exist, adding a placeholder selector
    private String errorMessageSelector = ".error-message"; // Example, update if different

    // Constructor
    public LoginPage(Page page) {
        super(page); // Pass page to BasePage constructor
        // System.out.println("LoginPage Initialized with Playwright Page."); // Logging can be added via Log4j2
    }

    public void enterUsername(String username) {
        if (username != null) {
            fill(usernameInputSelector, username);
        } else {
            // Consider logging a warning or error if username is null
            System.err.println("Username provided to enterUsername is null.");
        }
    }

    public void enterPassword(String password) {
        if (password != null) {
            fill(passwordInputSelector, password);
        } else {
            // Consider logging a warning or error if password is null
            System.err.println("Password provided to enterPassword is null.");
        }
    }

    public void clickLoginButton() {
        click(loginButtonSelector);
    }

    /**
     * Performs login action and returns a new HomePage instance.
     * This assumes successful login navigates to the HomePage.
     */
    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        // Return a new instance of HomePage, passing the current Page object
        return new HomePage(this.page);
    }

    /**
     * Attempts login and returns the current LoginPage instance.
     * Useful for testing scenarios where login is expected to fail and stay on the same page.
     */
    public LoginPage loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        return this; // Return current page instance
    }

    public String getErrorMessage() {
        if (isVisible(errorMessageSelector)) {
            return textContent(errorMessageSelector);
        }
        return "Error message selector not visible or not found.";
    }

    public String getPageTitle() {
        return page.title();
    }
}
