package com.thelak.smtp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.thelak")
public class SmtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmtpApplication.class, args);
    }

}
