package com.thelak.category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.thelak")
public class CategoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CategoryApplication.class, args);
    }

}
