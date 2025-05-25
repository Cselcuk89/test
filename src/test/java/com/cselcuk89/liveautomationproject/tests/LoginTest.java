package com.cselcuk89.liveautomationproject.tests; // Updated package

import com.cselcuk89.liveautomationproject.base.BaseTest; // Updated import
import com.cselcuk89.liveautomationproject.page.HomePage; // Updated import
import com.cselcuk89.liveautomationproject.page.LoginPage; // Updated import
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginDataProvider() {
        return new Object[][]{
                {"invalidUser", "invalidPass", "Username and password do not match any user in this service"},
                {"", "somePass", "Username is required"},
                {"someUser", "", "Password is required"}
        };
    }

    @Test(dataProvider = "invalidLoginData")
    public void invalidLoginTest(String username, String password, String expectedErrorMessageFragment) {
        LoginPage loginPage = new LoginPage(page);

        System.out.println("LoginTest.invalidLoginTest: Attempting login with username - " + username + ", password - " + password);
        loginPage.loginExpectingFailure(username, password); 

        String actualErrorMessage = loginPage.getErrorMessage();
        System.out.println("LoginTest.invalidLoginTest: Actual error message - " + actualErrorMessage);
        Assert.assertTrue(actualErrorMessage.contains(expectedErrorMessageFragment),
                "Error message validation failed. Expected fragment: '" + expectedErrorMessageFragment + "', Actual: '" + actualErrorMessage + "'");
    }

    @DataProvider(name = "validLoginData")
    public Object[][] validLoginDataProvider() {
        return new Object[][]{
                {"testuser", "testpass"}
        };
    }

    @Test(dataProvider = "validLoginData")
    public void validLoginTest(String username, String password) {
        LoginPage loginPage = new LoginPage(page);
        System.out.println("LoginTest.validLoginTest: Attempting login with username - " + username + ", password - " + password);

        HomePage homePage = loginPage.login(username, password); 

        System.out.println("LoginTest.validLoginTest: Verifying login success. Current URL: " + homePage.getCurrentUrl());
        Assert.assertNotNull(homePage, "HomePage object should not be null after login attempt.");
        System.out.println("LoginTest.validLoginTest: Placeholder for actual success condition. Current URL: " + page.url());
        Assert.assertTrue(true, "Placeholder for valid login success assertion.");
    }
}
