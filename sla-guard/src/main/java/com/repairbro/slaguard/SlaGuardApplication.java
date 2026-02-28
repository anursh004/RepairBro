package com.repairbro.slaguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "com.repairbro.slaguard", "com.repairbro.commons.exception" })
public class SlaGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(SlaGuardApplication.class, args);
    }
}
