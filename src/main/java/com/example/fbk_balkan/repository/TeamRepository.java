package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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



    @Query("SELECT t FROM Team t WHERE t.ageGroup LIKE %:birthYear% AND t.gender = :gender AND t.active = true")
    List<Team> findActiveTeamsByBirthYearAndGender(
            @Param("birthYear") String birthYear,
            @Param("gender") Team.Gender gender
    );

    /**
     * find by birth year only (ignoring gender), used for MIXED teams
     * or when no gender-specific team is found.
     */
    @Query("SELECT t FROM Team t WHERE t.ageGroup LIKE %:birthYear% AND t.active = true")
    List<Team> findActiveTeamsByBirthYear(@Param("birthYear") String birthYear);


}