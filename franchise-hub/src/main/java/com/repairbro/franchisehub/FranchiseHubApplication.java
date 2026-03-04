package com.repairbro.franchisehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.repairbro.franchisehub", "com.repairbro.commons" })
public class FranchiseHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(FranchiseHubApplication.class, args);
    }
}
