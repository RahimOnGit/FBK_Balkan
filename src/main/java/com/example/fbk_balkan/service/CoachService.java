package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.Coach;
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

    public Optional<Coach> findByUsername(String username) {
        return coachRepository.findByUsername(username);
    }

    public Coach save(Coach coach) {
        coach.setPassword(passwordEncoder.encode(coach.getPassword()));
        return coachRepository.save(coach);
    }
}
