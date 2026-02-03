package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrialRegistrationRepository extends JpaRepository<TrialRegistration, Long> {


    long countByStatus(TrialStatus status);

    List<TrialRegistration> findTop10ByOrderByCreatedAtDesc();

    List<TrialRegistration> findByStatusOrderByCreatedAtDesc(TrialStatus status);
}