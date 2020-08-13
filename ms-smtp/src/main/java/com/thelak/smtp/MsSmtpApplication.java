package com.thelak.smtp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.thelak")
public class MsSmtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSmtpApplication.class, args);
    }

}
