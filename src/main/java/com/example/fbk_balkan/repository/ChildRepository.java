package com.example.fbk_balkan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fbk_balkan.entity.Child;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    @Query("SELECT c FROM Child c JOIN c.parentChildren pc WHERE pc.parent.userId = :parentId")
    List<Child> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT DISTINCT c FROM Child c " +
            "JOIN c.teamMemberships tm " +
            "JOIN tm.team t " +
            "JOIN t.coachTeams ct " +
            "WHERE ct.coach.userId = :coachId")
    List<Child> findChildrenForCoach(@Param("coachId") Long coachId);
}

