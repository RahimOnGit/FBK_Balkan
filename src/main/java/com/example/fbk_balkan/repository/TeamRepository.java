package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Check if a team exists with given name and age group
     */
    boolean existsByNameAndAgeGroup(String name, String ageGroup);

    /**
     * Find all teams for a specific coach
     */
    List<Team> findByCoachId(Long coachId);

    /**
     * Find all teams for a specific coach by email
     */
    List<Team> findByCoachEmail(String email);
}