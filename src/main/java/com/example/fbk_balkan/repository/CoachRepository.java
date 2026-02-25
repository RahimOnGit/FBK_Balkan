package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
