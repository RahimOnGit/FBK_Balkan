package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    Optional<Coach> findByUsername(String username);
    boolean existsByUsername(String username);
}
