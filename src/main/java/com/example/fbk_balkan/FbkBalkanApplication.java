package com.example.fbk_balkan;

import com.example.fbk_balkan.service.TrialRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FbkBalkanApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(FbkBalkanApplication.class, args);
    }
@Autowired
    TrialRegistrationService service;
    public void run(String... args) throws Exception {

    }
}
