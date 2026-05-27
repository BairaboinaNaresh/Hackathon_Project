package com.hackathonProject.pages;

import com.hackathonProject.base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.hackathonProject.utils.JavaScriptUtil;
import com.hackathonProject.utils.WaitUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageCoursesPage {

    private static final Logger logger = LogManager.getLogger(LanguageCoursesPage.class);
    private static final String PAGE_URL = "https://www.coursera.org/courses?query=language+learning";
    private static final Pattern NAME_COUNT = Pattern.compile("^(.+?)\\s*\\(([\\d,]+)\\)");
    private static final Set<String> KNOWN_LEVELS = Set.of("Beginner", "Intermediate", "Advanced", "Mixed");

    private static final By LANGUAGE_SECTION = By.xpath("//button[normalize-space(.)='Language']");
    private static final By LEVEL_SECTION = By.xpath("//button[normalize-space(.)='Level']");
    private static final By FILTER_SORT_BTN = By.xpath("//button[contains(.,'Filter')]");
    private static final By FILTER_LABELS = By.xpath("//label[.//input[@type='checkbox']]");
    private static final By VISIBLE_LABEL = By.xpath("//label[contains(text(),'(')]");
    private static final By SHOW_MORE_BTN = By.xpath("//button[contains(.,'Show more')]");
    private static final By CLOSE_POPUP = By.xpath("//button[@aria-label='Close']");

    private WebDriver driver;

    // Initializes WebDriver instance from BaseClass.
    // Prepares page for filter interactions.
    public LanguageCoursesPage() {
        this.driver = BaseClass.getDriver();
    }


    // Navigates to Language Learning courses page.
    // Waits for page load and closes popup if present.
    public void navigateToLanguageLearning() {
        driver.get(PAGE_URL);
        WaitUtil.waitForPageLoad(driver);
        dismissPopup();
    }

    // Extracts all available language filter options.
    // Opens filter panel, expands list, and collects language counts.
    public Map<String, Integer> extractAllLanguages() {
        logger.info("Extracting all language filter options");
        Map<String, Integer> map = new LinkedHashMap<>();

        openFilterPanel();
        if (!clickSection(LANGUAGE_SECTION, "Language")) return map;
        clickShowMore();

        for (String[] entry : getFilterEntries()) {
            if (!isLevel(entry[0])) map.put(entry[0], Integer.parseInt(entry[1]));
        }
        logger.info("Total languages: " + map.size());
        return map;
    }


    // Extracts all available level filter options.
    // Reloads page to ensure clean filter state before extraction.
    public Map<String, Integer> extractAllLevels() {
        logger.info("Extracting all level filter options");
        Map<String, Integer> map = new LinkedHashMap<>();

        // Full reload for clean filter state
        driver.navigate().to(PAGE_URL);
        WaitUtil.waitForPageLoad(driver);
        dismissPopup();

        openFilterPanel();
        if (!clickSection(LEVEL_SECTION, "Level")) return map;

        for (String[] entry : getFilterEntries()) {
            if (isLevel(entry[0])) {
                map.put(entry[0], Integer.parseInt(entry[1]));
                logger.info("Level: " + entry[0] + " → " + entry[1]);
            }
        }
        logger.info("Total levels: " + map.size());
        return map;
    }

    // Opens filter panel if not already visible.
    // Uses scroll and JavaScript click for reliability.
    private void openFilterPanel() {
        if (WaitUtil.waitForElement(driver, LANGUAGE_SECTION, 3) != null) return;

        WebElement filterBtn = WaitUtil.waitForElement(driver, FILTER_SORT_BTN, 8);
        if (filterBtn != null) {
            JavaScriptUtil.scrollAndClick(driver, filterBtn);
            WaitUtil.waitForElement(driver, LANGUAGE_SECTION, 5);
            logger.info("Opened Filter panel");
        }
    }


    // Clicks specified filter section like Language or Level.
    // Verifies section availability before interaction.
    private boolean clickSection(By locator, String name) {
        WebElement btn = WaitUtil.waitForElement(driver, locator, 5);

        if (btn == null || btn.getText().contains("Filter")) {
            logger.warn("'" + name + "' section not found");
            return false;
        }

        JavaScriptUtil.scrollAndClick(driver, btn);
        WaitUtil.waitForElement(driver, VISIBLE_LABEL, 5);
        logger.info("Clicked '" + name + "' section");
        return true;
    }

    // Clicks "Show more" button if available.
    // Expands full list of filter options.
    private void clickShowMore() {
        WebElement btn = WaitUtil.waitForElement(driver, SHOW_MORE_BTN, 3);
        if (btn != null) JavaScriptUtil.scrollAndClick(driver, btn);
    }


    // Reads visible filter labels and extracts name and count.
    // Removes duplicates and ignores invalid entries.
    private List<String[]> getFilterEntries() {
        List<String[]> results = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (WebElement label : driver.findElements(FILTER_LABELS)) {
            if (!label.isDisplayed()) continue;
            String text = label.getText().trim();
            if (text.isEmpty() || !text.contains("(")) continue;

            Matcher m = NAME_COUNT.matcher(text.replaceAll("[\\r\\n]+", " ").trim());
            if (m.find()) {
                String name = m.group(1).trim();
                if (!name.isEmpty() && seen.add(name)) {
                    results.add(new String[]{name, m.group(2).replace(",", "")});
                }
            }
        }
        return results;
    }


    // Checks whether the given name matches a known course level.
    // Used to separate language and level filters.
    private boolean isLevel(String name) {
        return name != null && KNOWN_LEVELS.stream()
            .anyMatch(l -> name.trim().equalsIgnoreCase(l));
    }

    // Closes popup if it appears on the page.
    // Prevents modal from blocking filter interactions
    private void dismissPopup() {
        try {
            WebElement btn = driver.findElement(CLOSE_POPUP);
            if (btn.isDisplayed()) btn.click();
        } catch (Exception ignored) {}
    }
}