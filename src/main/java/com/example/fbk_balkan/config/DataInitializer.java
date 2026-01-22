package com.example.fbk_balkan.config;

import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Create a coach if one doesn't already exist
        if (!coachRepository.existsByUsername("coach")) {
            Coach coach = new Coach();
            coach.setUsername("coach");
            coach.setPassword(passwordEncoder.encode("password"));
            coach.setEmail("coach@fbkbalkan.se");
            coach.setRole("COACH");
            coach.setEnabled(true);
            coachRepository.save(coach);

            System.out.println("Default coach created");
        }

        // Create a socialadmin if one doesn't already exist
        if (!coachRepository.existsByUsername("socialadmin")) {
            Coach socialAdmin = new Coach();
            socialAdmin.setUsername("socialadmin");
            socialAdmin.setPassword(passwordEncoder.encode("password"));
            socialAdmin.setEmail("social@fbkbalkan.se");
            socialAdmin.setRole("SOCIAL_ADMIN,ADMIN");
            socialAdmin.setEnabled(true);
            coachRepository.save(socialAdmin);

            System.out.println("Social admin created");
        }
    }
}
