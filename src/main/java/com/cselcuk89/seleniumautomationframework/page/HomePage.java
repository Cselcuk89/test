package com.cselcuk89.seleniumautomationframework.page;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

@Component
public class HomePage extends BasePage {

    // Selectors for elements based on previous @FindBy
    private String logoutButtonSelector = "#logoutButton"; // Placeholder ID
    private String welcomeMessageHeaderSelector = "h1";   // Placeholder for a welcome message or header

    // Constructor
    public HomePage(Page page) {
        super(page); // Pass page to BasePage constructor
        // System.out.println("HomePage Initialized with Playwright Page."); // Logging can be added via Log4j2
    }

    public String getHomePageTitle() {
        return page.title();
    }

    public boolean isLogoutButtonDisplayed() {
        return isVisible(logoutButtonSelector);
    }

    public String getWelcomeMessage() {
        if (isVisible(welcomeMessageHeaderSelector)) {
            return textContent(welcomeMessageHeaderSelector);
        }
        return "Welcome message header not visible or not found.";
    }

    /**
     * Clicks the logout button.
     * Assumes successful logout navigates to the LoginPage.
     * @return A new LoginPage instance.
     */
    public LoginPage clickLogoutButton() {
        if (isLogoutButtonDisplayed()) {
            click(logoutButtonSelector);
            // Return a new instance of LoginPage, passing the current Page object
            return new LoginPage(this.page);
        } else {
            System.err.println("Logout button is not displayed or not found on HomePage.");
            // Depending on desired behavior, could throw an exception or return null,
            // or return a new LoginPage if the click is attempted regardless of visibility.
            // For now, let's assume if not displayed, we might not want to proceed to LoginPage.
            // This behavior might need refinement based on application flow.
            // If the application ALWAYS navigates to login page on trying to logout, even if button is hidden by error:
            // click(logoutButtonSelector); // attempt click anyway or log error
            return new LoginPage(this.page); // Or handle error more gracefully
        }
    }
}
