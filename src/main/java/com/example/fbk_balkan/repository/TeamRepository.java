package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team , Long> {
//    all crud methods are provided by JpaRepository
boolean existsByNameAndAgeGroup(String name, String ageGroup);

List<Team> findAllByOrderByCreatedDateDesc();

}