package com.example.fbk_balkan.config;

import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User coach = new User();
            coach.setFirstName("Rahim");
            coach.setLastName("Elhaj");
            coach.setEmail("coach@fbkbalkan.se");
            coach.setPassword(passwordEncoder.encode("password"));
            coach.setRole(Role.COACH);
            coach.setEnabled(true);
            userRepository.save(coach);
        }

        // Create a default social admin if none exists
        if (!userRepository.findByEmail("social@fbkbalkan.se").isPresent()) {
            User socialAdmin = new User();
//            socialAdmin.setUsername("socialadmin");
            socialAdmin.setLastName("Khalid");
            socialAdmin.setFirstName("IB");

            socialAdmin.setPassword(passwordEncoder.encode("password"));
            socialAdmin.setEmail("social@fbkbalkan.se");
            socialAdmin.setRole(Role.ADMIN);
            socialAdmin.setEnabled(true);
            userRepository.save(socialAdmin);
            System.out.println("Default social admin created - : +"+socialAdmin.getEmail()+", Password: password");
        }

        // Create a default admin if none exists
        if (!userRepository.findByEmail("admin@fbkbalkan.se").isPresent()) {
            User admin = new User();
//            admin.setUsername("admin");
            admin.setLastName("Semo");
            admin.setFirstName("Saif");

            admin.setPassword(passwordEncoder.encode("password"));
            admin.setEmail("admin@fbkbalkan.se");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Default admin created - :"+ admin.getEmail()   +", Password: password");
        }
        //Create a default assistant coach if none exists
        if (!userRepository.findByEmail("assistant@fbkbalkan.se").isPresent()) {
            User assistant = new User();
//            admin.setUsername("admin");
            assistant.setLastName("Kh");
            assistant.setFirstName("Hadia");

            assistant.setPassword(passwordEncoder.encode("password"));
            assistant.setEmail("assistant@fbkbalkan.se");
            assistant.setRole(Role.ASSISTANT_COACH);
            assistant.setEnabled(true);
            userRepository.save(assistant);
            System.out.println("Default admin created - :"+ assistant.getEmail()   +", Password: password");
        }
    }
}
