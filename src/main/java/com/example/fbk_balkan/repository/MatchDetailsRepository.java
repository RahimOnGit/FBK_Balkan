package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.MatchDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchDetailsRepository extends JpaRepository<MatchDetails, Long> {

    Optional<MatchDetails> findByGameNumber(Long gameNumber);
    boolean existsByGameNumber(Long gameNumber);
}