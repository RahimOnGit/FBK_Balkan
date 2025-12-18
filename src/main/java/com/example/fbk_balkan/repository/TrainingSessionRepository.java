package com.example.fbk_balkan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fbk_balkan.entity.TrainingSession;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}

