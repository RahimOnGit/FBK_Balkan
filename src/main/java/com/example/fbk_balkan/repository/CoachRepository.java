package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    Optional<Coach> findByEmail(String email);
    boolean existsByEmail(String email);

    List<Coach> findByRole(Role role);
}
