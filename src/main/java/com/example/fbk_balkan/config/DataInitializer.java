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
    public void run(String... args) throws Exception {
        // Create a default coach if none exists
        if (coachRepository.count() == 0) {
            Coach coach = new Coach();
            coach.setUsername("coach");
            coach.setPassword(passwordEncoder.encode("password"));
            coach.setEmail("coach@fbkbalkan.se");
            coach.setRole("COACH");
            coach.setEnabled(true);
            coachRepository.save(coach);
            System.out.println("Default coach created - Username: coach, Password: password");
        }

        // Create a default social admin if none exists
        if (!coachRepository.findByUsername("socialadmin").isPresent()) {
            Coach socialAdmin = new Coach();
            socialAdmin.setUsername("socialadmin");
            socialAdmin.setPassword(passwordEncoder.encode("password"));
            socialAdmin.setEmail("social@fbkbalkan.se");
            socialAdmin.setRole("SOCIAL_ADMIN");
            socialAdmin.setEnabled(true);
            coachRepository.save(socialAdmin);
            System.out.println("Default social admin created - Username: socialadmin, Password: password");
        }

        // Create a default admin if none exists
        if (!coachRepository.findByUsername("admin").isPresent()) {
            Coach admin = new Coach();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setEmail("admin@fbkbalkan.se");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            coachRepository.save(admin);
            System.out.println("Default admin created - Username: admin, Password: password");
        }
    }
}
