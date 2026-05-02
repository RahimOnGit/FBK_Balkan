package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.dto.match.MatchDetailsDTO;
import com.example.fbk_balkan.entity.Match;
import com.example.fbk_balkan.entity.MatchDetails;
import com.example.fbk_balkan.mapper.MatchDetailsMapper;
import com.example.fbk_balkan.mapper.MatchMapper;
import com.example.fbk_balkan.repository.MatchDetailsRepository;
import com.example.fbk_balkan.repository.MatchRepository;
import com.example.fbk_balkan.service.external.SvffApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchMapper matchMapper;
    @Autowired
    private MatchDetailsRepository matchDetailsRepository;

    @Autowired
    private MatchDetailsMapper matchDetailsMapper;
    @Autowired
    private SvffApiService svffApiService;




    public List<GameDTO> fetchMatches() {
        List<Match> games = matchRepository.findAll();
        return games.stream()
                .map(matchMapper::toDto)
                .toList();
    }

    public List<GameDTO> fetchUpcomingMatchesWithinMonths(int months) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusMonths(months);
        return matchRepository.findAll().stream()
                .map(matchMapper::toDto)
                .filter(m -> m.timeAsDateTime() != null
                        && !m.timeAsDateTime().isBefore(now)
                        && !m.timeAsDateTime().isAfter(cutoff))
                .sorted(Comparator.comparing(GameDTO::timeAsDateTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<GameDTO> fetchMatchesForTeam(Long svffTeamId) {
        if (svffTeamId == null) return List.of();

        return matchRepository
                .findByHomeTeamSvffIdOrAwayTeamSvffId(svffTeamId, svffTeamId)
                .stream()
                .map(matchMapper::toDto)
                .toList();
    }

    public List<GameDTO> fetchUpcomingMatchesForTeam(Long svffTeamId) {
        if (svffTeamId == null) return List.of();
        LocalDateTime now = LocalDateTime.now();
        return matchRepository
                .findByHomeTeamSvffIdOrAwayTeamSvffId(svffTeamId, svffTeamId)
                .stream()
                .map(matchMapper::toDto)
                .filter(m -> m.timeAsDateTime() != null && m.timeAsDateTime().isAfter(now))
                .sorted(Comparator.comparing(GameDTO::timeAsDateTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<GameDTO> fetchRecentResultsForTeam(Long svffTeamId) {
        if (svffTeamId == null) return List.of();
        LocalDateTime now = LocalDateTime.now();
        return matchRepository
                .findByHomeTeamSvffIdOrAwayTeamSvffId(svffTeamId, svffTeamId)
                .stream()
                .map(matchMapper::toDto)
                .filter(m -> m.timeAsDateTime() != null
                        && m.timeAsDateTime().isBefore(now)
                        && m.goalsScoredHomeTeam() != null
                        && m.goalsScoredHomeTeam() != -1)
                .sorted(Comparator.comparing(GameDTO::timeAsDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }
}
