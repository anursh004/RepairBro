package com.repairbro.fixbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.repairbro.fixbill",
        "com.repairbro.commons"
})
public class FixBillApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixBillApplication.class, args);
    }
}
