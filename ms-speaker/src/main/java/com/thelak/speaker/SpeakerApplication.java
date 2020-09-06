package com.thelak.speaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.thelak")
public class SpeakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeakerApplication.class, args);
    }

}
