package com.example.fbk_balkan;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//1.gives the mail sending asynchronous - 2. to fetch game daily
@EnableAsync
public class FbkBalkanApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(FbkBalkanApplication.class, args);
    }
    public void run(String... args) throws Exception {

    }
}
