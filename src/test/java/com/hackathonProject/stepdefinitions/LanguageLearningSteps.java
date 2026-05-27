package com.hackathonProject.stepdefinitions;

import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.asserts.SoftAssert;

import com.hackathonProject.pages.LanguageCoursesPage;
import com.hackathonProject.utils.ExtentReportManager;

import java.util.Map;

public class LanguageLearningSteps {

    private static final Logger logger = LogManager.getLogger(LanguageLearningSteps.class);

    private LanguageCoursesPage languageCoursesPage = new LanguageCoursesPage();
    private Map<String, Integer> extractedLanguages;
    private Map<String, Integer> extractedLevels;
    private SoftAssert softAssert = new SoftAssert();

    @Given("the user navigates to the Language Learning category")
    public void theUserNavigatesToLanguageLearning() {
        logger.info("STEP: Navigating to Language Learning category");
        ExtentReportManager.logInfo("Navigating to Language Learning");
        languageCoursesPage.navigateToLanguageLearning();
    }

    @When("the user opens the language filter dropdown")
    public void theUserOpensLanguageFilterDropdown() {
        logger.info("STEP: Opening Language filter dropdown");
        ExtentReportManager.logInfo("Extracting language filter options");
        extractedLanguages = languageCoursesPage.extractAllLanguages();
    }

    @Then("all available languages with their counts should be captured and displayed")
    public void allLanguagesShouldBeCaptured() {
        logger.info("STEP: Verifying languages were captured");

        // PASS: if extractedLanguages map is NOT null (data was successfully extracted)
        // FAIL: if extractedLanguages is null → extraction method failed or did not run properly
        softAssert.assertNotNull(extractedLanguages, "Language map is null");
        // PASS: if extractedLanguages map has at least 1 entry (languages found)
        // FAIL: if map is null OR empty → no languages were extracted from UI
        softAssert.assertFalse(
            extractedLanguages == null || extractedLanguages.isEmpty(),
            "No languages were extracted"
        );

        StringBuilder sb = new StringBuilder("\n===== LANGUAGES EXTRACTED =====\n");
        if (extractedLanguages != null) {
            extractedLanguages.forEach((lang, count) ->
                sb.append(String.format("  %-25s → %d courses%n", lang, count)));
        }
        sb.append("================================");

        logger.info(sb.toString());
        ExtentReportManager.logInfo(sb.toString());
        softAssert.assertAll();

    }

    @When("the user opens the level filter dropdown")
    public void theUserOpensLevelFilterDropdown() {
        logger.info("STEP: Opening Level filter dropdown");
        ExtentReportManager.logInfo("Extracting level filter options");
        extractedLevels = languageCoursesPage.extractAllLevels();
    }

    @Then("all available levels with their counts should be captured and displayed")
    public void allLevelsShouldBeCaptured() {
        logger.info("STEP: Verifying levels were captured");
    // PASS: if extractedLevels map is NOT null (levels were fetched successfully)
    // FAIL: if extractedLevels is null → extraction failed or method issue
        softAssert.assertNotNull(extractedLevels, "Level map is null");

        StringBuilder sb = new StringBuilder("\n===== LEVELS EXTRACTED =====\n");
        if (extractedLevels != null) {
            extractedLevels.forEach((level, count) ->
                sb.append(String.format("  %-25s → %d courses%n", level, count)));
        }
        sb.append("============================");

        logger.info(sb.toString());
        ExtentReportManager.logInfo(sb.toString());
        softAssert.assertAll();

    }

    @Then("the language list should contain at least {int} languages")
    public void languageListShouldHaveAtLeast(int minimum) {
        logger.info("STEP: Asserting language count >= " + minimum);
        int actual = extractedLanguages != null ? extractedLanguages.size() : 0;

        if (actual < minimum) {
            logger.warn("Expected >= " + minimum + " languages but found " + actual);
            ExtentReportManager.logWarning("Language count (" + actual + ") below minimum (" + minimum + ")");
        } else {
            ExtentReportManager.logPass("Language count: " + actual + " (minimum: " + minimum + ")");
            logger.info("Language count: " + actual + " >= " + minimum);
        }

        // PASS: if number of extracted languages is greater than or equal to minimum value
        // FAIL: if extractedLanguages count is less than minimum → insufficient data loaded
        softAssert.assertTrue(actual >= minimum,
            "Expected >= " + minimum + " languages but found " + actual);
        softAssert.assertAll();
    }

    @Then("the level list should contain at least {int} levels")
    public void levelListShouldHaveAtLeast(int minimum) {
        logger.info("STEP: Asserting level count >= " + minimum);
        int actual = extractedLevels != null ? extractedLevels.size() : 0;

        if (actual < minimum) {
            logger.warn("Expected >= " + minimum + " levels but found " + actual);
            ExtentReportManager.logWarning("Level count (" + actual + ") below minimum (" + minimum + ")");
        } else {
            ExtentReportManager.logPass("Level count: " + actual + " (minimum: " + minimum + ")");
            logger.info("Level count: " + actual + " >= " + minimum);
        }

        // PASS: if number of extracted levels is greater than or equal to minimum value
        // FAIL: if extractedLevels count is less than minimum → expected levels missing
        softAssert.assertTrue(actual >= minimum,
            "Expected >= " + minimum + " levels but found " + actual);


        softAssert.assertAll();
    }
}