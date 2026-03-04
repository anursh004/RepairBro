package com.repairbro.insightengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.repairbro.insightengine", "com.repairbro.commons" })
public class InsightEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(InsightEngineApplication.class, args);
    }
}
