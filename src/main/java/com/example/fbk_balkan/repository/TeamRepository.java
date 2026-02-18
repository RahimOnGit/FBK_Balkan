package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team , Long> {
//    all crud methods are provided by JpaRepository
boolean existsByNameAndAgeGroup(String name, String ageGroup);
//Get all public activ teams
    List<Team> findByActiveTrue();
    //Get one active public team safely
    Optional<Team> findByIdAndActiveTrue(Long id);


}
