package com.example.fbk_balkan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fbk_balkan.entity.ParentChild;

import java.util.List;

@Repository
public interface ParentChildRepository extends JpaRepository<ParentChild, Long> {
    @Query("SELECT pc FROM ParentChild pc WHERE pc.parent.userId = :parentId")
    List<ParentChild> findByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT pc FROM ParentChild pc WHERE pc.child.childId = :childId")
    List<ParentChild> findByChildId(@Param("childId") Long childId);
}

