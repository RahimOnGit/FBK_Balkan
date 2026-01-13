package com.example.fbk_balkan.controller;
import com.example.fbk_balkan.dto.TeamSelectDTO;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;




import java.util.List;




@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
public class TeamController {


    private final TeamService teamService;
    private final UserRepository userRepository;


    @GetMapping("/teams/select")
    public ResponseEntity<List<TeamSelectDTO>> getCoachTeams(Authentication authentication) {


        // 1. Get username/email from Spring Security
        String email = authentication.getName();


        // 2. Fetch actual User entity from database
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Coach not found"));


        // 3. Ensure only coaches access this endpoint
        if (coach.getRole() != User.Role.COACH) {
            throw new AccessDeniedException("Only coaches allowed");
        }


        // 4. Get teams
        List<TeamSelectDTO> teams = teamService.getTeamsForCoach(coach);


        return ResponseEntity.ok(teams);
    }




}
