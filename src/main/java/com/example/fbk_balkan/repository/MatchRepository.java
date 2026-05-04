package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match,Long> {

//for duplicated matches
    boolean existsByGameNumber(Long gameNumber);
//fetch matches by team

    java.util.Optional<Match> findByGameNumber(Long gameNumber);

    List<Match> findByHomeTeamSvffIdOrAwayTeamSvffId(Long homeTeamSvffId, Long awayTeamSvffId);


}
