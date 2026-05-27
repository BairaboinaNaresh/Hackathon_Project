package com.hackathonProject.listeners;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;

public class CucumberListener implements ConcurrentEventListener {

    private static final Logger logger = LogManager.getLogger(CucumberListener.class);


// Registers event handlers for different Cucumber lifecycle events.
    // Maps events like scenario start, step finish, and run completion.
    // Ensures custom logging is triggered during execution.
    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::onScenarioStart);
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onScenarioFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::onRunFinished);
    }

    // Executes when a scenario starts.
    // Logs scenario name and feature file location.
    // Helps track execution flow in logs.
    private void onScenarioStart(TestCaseStarted event) {
        String scenarioName = event.getTestCase().getName();
        String uri = event.getTestCase().getUri().toString();
        logger.info("▶ SCENARIO STARTED: [" + scenarioName + "] in [" + uri + "]");
    }


    // Executes after each step finishes.
    // Logs step result such as PASSED, FAILED, SKIPPED, or PENDING.
    // Captures step-level details for debugging.
    private void onStepFinished(TestStepFinished event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep step = (PickleStepTestStep) event.getTestStep();
            String stepText = step.getStep().getText();
            Result result = event.getResult();

            switch (result.getStatus()) {
                case PASSED:
                    logger.info("   STEP PASSED: " + stepText);
                    break;
                case FAILED:
                    logger.error("   STEP FAILED: " + stepText);
                    if (result.getError() != null) {
                        logger.error("     Error: " + result.getError().getMessage());
                    }
                    break;
                case SKIPPED:
                    logger.warn("   STEP SKIPPED: " + stepText);
                    break;
                case PENDING:
                    logger.warn("   STEP PENDING: " + stepText);
                    break;
                default:
                    logger.info("   STEP [" + result.getStatus() + "]: " + stepText);
            }
        }
    }


    // Executes after a scenario completes.
    // Logs final result and execution time for the scenario.
    // Helps measure test performance and outcome.
    private void onScenarioFinished(TestCaseFinished event) {
        String scenarioName = event.getTestCase().getName();
        Status status = event.getResult().getStatus();
        double durationSeconds = event.getResult().getDuration().toNanos() / 1_000_000_000.0;

        if (status == Status.PASSED) {
            logger.info(String.format("SCENARIO PASSED: [%s] in %.2fs", scenarioName, durationSeconds));
        } else {
            logger.error(String.format("SCENARIO FAILED: [%s] in %.2fs | Status: %s",
                scenarioName, durationSeconds, status));
        }
    }


    // Executes when the entire test run finishes.
    // Flushes Extent Reports and performs report archiving.
    // Ensures final reporting and cleanup is completed.

    private void onRunFinished(TestRunFinished event) {
        logger.info("  CUCUMBER TEST RUN FINISHED");


        // Flush Extent Reports
        try {
            com.hackathonProject.utils.ExtentReportManager.flushReports();
            logger.info("Extent Reports flushed successfully");
        } catch (Exception e) {
            logger.error("Could not flush Extent Reports: " + e.getMessage());
        }


    }
}