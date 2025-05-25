package com.cselcuk89.liveautomationproject; // Updated package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SeleniumAutomationFrameworkApplication { // Class name remains the same

    public static void main(String[] args) {
        // SpringApplication.run will refer to this class in the new package
        SpringApplication.run(SeleniumAutomationFrameworkApplication.class, args);
    }

}
