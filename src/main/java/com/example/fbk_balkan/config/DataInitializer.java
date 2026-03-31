package com.example.fbk_balkan.config;

import com.example.fbk_balkan.dto.svff.SvffTeamDto;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.external.SvffApiService;
import com.example.fbk_balkan.service.external.SvffTeamConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final TeamRepository teamRepository;           // ← ADD
    private final SvffApiService svffApiService;           // ← ADD
    private final SvffTeamConverter svffTeamConverter;     // ← ADD

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
        initTeamsFromSvff();

        // Create a default social admin if none exists, or fix role if wrong
        var socialAdminOpt = userRepository.findByEmail("social@fbkbalkan.se");
        if (socialAdminOpt.isEmpty()) {
            User socialAdmin = new User();
            socialAdmin.setLastName("Khalid");
            socialAdmin.setFirstName("IB");
            socialAdmin.setPassword(passwordEncoder.encode("password"));
            socialAdmin.setEmail("social@fbkbalkan.se");
            socialAdmin.setRole(Role.SOCIAL_ADMIN);
            socialAdmin.setEnabled(true);
            userRepository.save(socialAdmin);
            System.out.println("Default social admin created: " + socialAdmin.getEmail() + ", Password: password");
        } else {
            User socialAdmin = socialAdminOpt.get();
            if (socialAdmin.getRole() != Role.SOCIAL_ADMIN) {
                socialAdmin.setRole(Role.SOCIAL_ADMIN);
                userRepository.save(socialAdmin);
                System.out.println("Fixed social admin role to SOCIAL_ADMIN for: " + socialAdmin.getEmail());
            }
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

    private void initTeamsFromSvff() {
        try {
            List<SvffTeamDto> svffTeams = svffApiService.fetchTeams();
            int created = 0;

            for (SvffTeamDto svff : svffTeams) {

                //skip teams with no numbers in name (i.e senior and hj -> skip , fbk balkan 2014 -> OK )
                if(!svff.getName().matches(".*\\d+.*"))
                {
                    System.out.println("Skipping team with no numbers in name: " + svff.getName());
                    continue;
                }

                // Skip if team already in DB by name
                if (teamRepository.existsByName(svff.getName())) {
                    System.out.println(" Skipping existing: " + svff.getName());
                    continue;
                }

                Team team = svffTeamConverter.toTeamEntity(svff);
                teamRepository.save(team);
                System.out.println("✅ Saved: " + team.getName() + " → ageGroup: " + team.getAgeGroup());
                created++;
            }

            System.out.println(" Done. " + created + " teams added from SvFF.");

        } catch (Exception e) {
            // Don't crash the app if API is down
            System.out.println(" SvFF sync failed: " + e.getMessage());
        }
    }
}
