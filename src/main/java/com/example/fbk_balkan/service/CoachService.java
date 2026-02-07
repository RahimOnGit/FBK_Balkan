package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.CoachResponseDto;
import com.example.fbk_balkan.dto.CreateCoachDto;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoachService {
    private final CoachRepository coachRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public CoachService(CoachRepository coachRepository, PasswordEncoder passwordEncoder) {
        this.coachRepository = coachRepository;
        this.passwordEncoder = passwordEncoder;
    }



//  create coach
    public Coach createCoach(CreateCoachDto dto)
    {
Coach coach = new Coach();
coach.setFirstName(dto.getFirstName());
coach.setLastName(dto.getLastName());
coach.setEmail(dto.getEmail());
coach.setPassword(passwordEncoder.encode(dto.getPassword()));
coach.setRole(dto.getRole()!=null ? dto.getRole(): Role.COACH);
coach.setEnabled(true);

return coachRepository.save(coach);
    }
}
