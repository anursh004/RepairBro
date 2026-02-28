package com.repairbro.repairsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.repairbro.repairsim", "com.repairbro.commons.exception" })
public class RepairSimApplication {
    public static void main(String[] args) {
        SpringApplication.run(RepairSimApplication.class, args);
    }
}
