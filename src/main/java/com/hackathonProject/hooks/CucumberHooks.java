package com.hackathonProject.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hackathonProject.base.BaseClass;
import com.hackathonProject.utils.ExtentReportManager;
import com.hackathonProject.utils.ScreenshotUtil;

public class CucumberHooks {

    private static final Logger logger = LogManager.getLogger(CucumberHooks.class);

    @Before
    public void setUp(Scenario scenario) {
        logger.info("====== SCENARIO STARTED: " + scenario.getName() + " ======");
        logger.info("Tags: " + scenario.getSourceTagNames());

        // Launch browser (reads 'browser' from config.properties)
        BaseClass.createDriver();

        // Initialize Extent Report test node for this scenario
        ExtentReportManager.createTest(scenario.getName());
        ExtentReportManager.logInfo("Browser launched for scenario: " + scenario.getName());

        logger.info("Browser launched successfully");
    }

    @After
    public void tearDown(Scenario scenario) {

        logger.info("SCENARIO FINISHED: " + scenario.getName()
                + " | Status: " + scenario.getStatus());

        String scenarioName = scenario.getName().replaceAll(" ", "_");

        // ✅ 1. Capture screenshot once
        byte[] screenshotBytes = ScreenshotUtil.captureScreenshotAsBytes(BaseClass.getDriver());
        String screenshotPath = ScreenshotUtil.captureScreenshot(BaseClass.getDriver(), scenarioName);

        // ✅ 2. Attach to Cucumber report (optional but recommended)
        if (screenshotBytes != null) {
            scenario.attach(screenshotBytes, "image/png", scenarioName);
        }

        // ✅ 3. Logging in Extent Report
        if (scenario.isFailed()) {
            logger.warn("Scenario FAILED — screenshot captured");

            ExtentReportManager.logFail("Scenario FAILED: " + scenario.getName());
            ExtentReportManager.attachScreenshot(screenshotPath);

        } else {
            logger.info("Scenario PASSED — screenshot captured");

            ExtentReportManager.logPass("Scenario PASSED: " + scenario.getName());
            ExtentReportManager.attachScreenshot(screenshotPath);
        }

        // ✅ 4. Always close browser
        BaseClass.removeDriver();
        logger.info("Browser closed. Scenario teardown complete.");
    }
}
