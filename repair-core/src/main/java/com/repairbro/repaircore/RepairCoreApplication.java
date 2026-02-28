package com.repairbro.repaircore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.repairbro.repaircore",
        "com.repairbro.commons.exception"
})
public class RepairCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepairCoreApplication.class, args);
    }
}
