package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.TrialRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrialRegistrationRepository extends JpaRepository<TrialRegistration, Long> {

}
