package com.example.fbk_balkan;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//gives the mail sending asynchronous
@EnableAsync
public class FbkBalkanApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(FbkBalkanApplication.class, args);
    }
    public void run(String... args) throws Exception {

    }
}
