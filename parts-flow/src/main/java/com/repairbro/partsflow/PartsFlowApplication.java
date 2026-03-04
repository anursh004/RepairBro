package com.repairbro.partsflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.repairbro.partsflow",
        "com.repairbro.commons"
})
public class PartsFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(PartsFlowApplication.class, args);
    }
}
