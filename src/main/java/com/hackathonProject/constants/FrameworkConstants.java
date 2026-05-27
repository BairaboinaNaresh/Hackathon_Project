package com.hackathonProject.constants;

import com.hackathonProject.utils.ConfigReader;

public class FrameworkConstants {

    // From config.properties
    public static final String BASE_URL = ConfigReader.getProperty("baseUrl");
    public static final String EXCEL_OUTPUT_PATH = ConfigReader.getProperty("excelOutputPath");

    private FrameworkConstants() {}
}