package com.example.fbk_balkan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fbk_balkan.entity.TeamMembership;

import java.util.List;

@Repository
public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    @Query("SELECT tm FROM TeamMembership tm WHERE tm.team.teamId = :teamId")
    List<TeamMembership> findByTeamId(@Param("teamId") Long teamId);
    
    @Query("SELECT tm FROM TeamMembership tm WHERE tm.child.childId = :childId")
    List<TeamMembership> findByChildId(@Param("childId") Long childId);
}

