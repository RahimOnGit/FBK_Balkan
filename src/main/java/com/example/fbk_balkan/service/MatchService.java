package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.entity.Match;
import com.example.fbk_balkan.mapper.MatchMapper;
import com.example.fbk_balkan.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchMapper matchMapper;

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

    public List<GameDTO> fetchRecentResultsWithinMonths(int months) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusMonths(months);
        return matchRepository.findAll().stream()
                .map(matchMapper::toDto)
                .filter(m -> m.timeAsDateTime() != null
                        && m.timeAsDateTime().isBefore(now)
                        && !m.timeAsDateTime().isBefore(cutoff)
                        && m.goalsScoredHomeTeam() != null
                        && m.goalsScoredHomeTeam() != -1)
                .sorted(Comparator.comparing(GameDTO::timeAsDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public LinkedHashMap<String, List<GameDTO>> groupByMonth(List<GameDTO> matches) {
        Locale swedish = Locale.of("sv", "SE");
        return matches.stream()
                .filter(m -> m.timeAsDateTime() != null)
                .collect(Collectors.groupingBy(
                        m -> {
                            String monthName = m.timeAsDateTime().getMonth()
                                    .getDisplayName(TextStyle.FULL, swedish);
                            return monthName.toUpperCase(swedish) + " " + m.timeAsDateTime().getYear();
                        },
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
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