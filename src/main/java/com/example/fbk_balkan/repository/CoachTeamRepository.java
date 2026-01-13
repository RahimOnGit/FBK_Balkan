package com.example.fbk_balkan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fbk_balkan.entity.CoachTeam;

import java.util.List;

@Repository
public interface CoachTeamRepository extends JpaRepository<CoachTeam, Long> {
    @Query("SELECT ct FROM CoachTeam ct WHERE ct.coach.userId = :coachId")
    List<CoachTeam> findByCoachId(@Param("coachId") Long coachId);
}

