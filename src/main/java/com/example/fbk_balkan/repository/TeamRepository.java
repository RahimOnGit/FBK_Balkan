package com.example.fbk_balkan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fbk_balkan.entity.Team;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("SELECT t FROM Team t JOIN t.coachTeams ct WHERE ct.coach.userId = :coachId")
    List<Team> findByCoachId(@Param("coachId") Long coachId);
}

