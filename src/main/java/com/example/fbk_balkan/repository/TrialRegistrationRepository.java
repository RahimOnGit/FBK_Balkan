package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.TrialRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrialRegistrationRepository extends JpaRepository<TrialRegistration, Long> {
    Optional<TrialRegistration> findByEmail(String email);
    boolean existsByEmail(String email);
}



