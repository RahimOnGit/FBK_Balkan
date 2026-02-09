package com.example.fbk_balkan.config;

import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.entity.Role;
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
//            coach.setUsername("coach");
            coach.setFirstName("Rahim");
            coach.setLastName("Elhaj");

            coach.setPassword(passwordEncoder.encode("password"));
            coach.setEmail("coach@fbkbalkan.se");
            coach.setRole(Role.COACH);
            coach.setEnabled(true);
            coachRepository.save(coach);
            System.out.println("Default coach created - Username: coach, Password: password");
        }

        // Create a default social admin if none exists
        if (!coachRepository.findByEmail("social@fbkbalkan.se").isPresent()) {
            Coach socialAdmin = new Coach();
//            socialAdmin.setUsername("socialadmin");
            socialAdmin.setLastName("Khalid");
            socialAdmin.setFirstName("IB");

            socialAdmin.setPassword(passwordEncoder.encode("password"));
            socialAdmin.setEmail("social@fbkbalkan.se");
            socialAdmin.setRole(Role.ADMIN);
            socialAdmin.setEnabled(true);
            coachRepository.save(socialAdmin);
            System.out.println("Default social admin created - : +"+socialAdmin.getEmail()+", Password: password");
        }

        // Create a default admin if none exists
        if (!coachRepository.findByEmail("admin@fbkbalkan.se").isPresent()) {
            Coach admin = new Coach();
//            admin.setUsername("admin");
            admin.setLastName("Semo");
            admin.setFirstName("Saif");

            admin.setPassword(passwordEncoder.encode("password"));
            admin.setEmail("admin@fbkbalkan.se");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            coachRepository.save(admin);
            System.out.println("Default admin created - :"+ admin.getEmail()   +", Password: password");
        }
    }
}
