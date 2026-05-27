package com.hackathonProject.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.hackathonProject.base.BaseClass;
import com.hackathonProject.utils.WaitUtil;

public class HomePage {

	private static final Logger logger = LogManager.getLogger(HomePage.class);
    private WebDriver driver;
 
    @FindBy(name = "query")
    private WebElement searchBox;
 
    @FindBy(xpath = "//button[@aria-label='Close']")
    private WebElement closePopupBtn;



    // Initializes WebDriver and PageFactory elements.
    // Prepares page objects for interaction.
    public HomePage() {
        this.driver = BaseClass.getDriver();
        PageFactory.initElements(driver, this);
    }

    // Opens the application home page using provided URL.
    // Waits for page load and closes popup if present.
    public void openHomePage(String url) {
        logger.info("Navigating to: " + url);
        driver.get(url);
        WaitUtil.waitForPageLoad(driver);
        dismissPopup();
    }

    // Searches for a given keyword using search box.
    // Handles popup, submits search, and waits for results.
    public void searchFor(String searchTerm) {
        logger.info("Searching for: " + searchTerm);
        dismissPopup();
        WaitUtil.waitForElementVisible(driver, searchBox);
        searchBox.click();
        searchBox.clear();
        searchBox.sendKeys(searchTerm);
        searchBox.sendKeys(Keys.ENTER);
        WaitUtil.waitForPageLoad(driver);
        dismissPopup();
        logger.info("Search submitted. URL: " + driver.getCurrentUrl());
    }

    // Closes popup if it appears on the page.
    // Prevents modal dialogs from blocking actions.
    private void dismissPopup() {
        try {
            if (closePopupBtn.isDisplayed()) closePopupBtn.click();
        } catch (Exception ignored) {}
    }
}