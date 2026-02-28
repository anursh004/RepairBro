package com.repairbro.diagflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.repairbro.diagflow",
        "com.repairbro.commons.exception"
})
public class DiagFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiagFlowApplication.class, args);
    }
}
