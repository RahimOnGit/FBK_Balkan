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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /** Distinct, non-blank competition names across all matches, sorted A→Z. */
    public List<String> getDistinctCompetitions() {
        return matchRepository.findAll().stream()
                .map(Match::getCompetitionName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    /** Distinct FBK Balkan team names appearing in any match (home or away), sorted A→Z. */
    public List<String> getDistinctFbkTeams() {
        return matchRepository.findAll().stream()
                .flatMap(m -> Stream.of(m.getHomeTeamName(), m.getAwayTeamName()))
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .filter(n -> n.toLowerCase(Locale.ROOT).contains("fbk balkan"))
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    /**
     * Filtered match list for the public "Alla matcher" page.
     *
     * @param view        "upcoming" (default), "results", or "all"
     * @param competition exact competition name to match (nullable)
     * @param team        team name (must appear as home OR away) (nullable)
     * @param search      free-text matched case-insensitively against home/away team names (nullable)
     */
    public List<GameDTO> filterMatches(String view, String competition, String team, String search) {
        final LocalDateTime now = LocalDateTime.now();
        final String v = (view == null || view.isBlank()) ? "upcoming" : view.toLowerCase(Locale.ROOT);
        final String comp = (competition == null || competition.isBlank()) ? null : competition.trim();
        final String tm = (team == null || team.isBlank()) ? null : team.trim();
        final String q = (search == null || search.isBlank()) ? null : search.trim().toLowerCase(Locale.ROOT);

        Stream<GameDTO> stream = matchRepository.findAll().stream()
                .map(matchMapper::toDto)
                .filter(m -> m.timeAsDateTime() != null);

        // View filter
        if ("upcoming".equals(v)) {
            stream = stream.filter(m -> !m.timeAsDateTime().isBefore(now));
        } else if ("results".equals(v)) {
            stream = stream.filter(m -> m.timeAsDateTime().isBefore(now)
                    && m.goalsScoredHomeTeam() != null
                    && m.goalsScoredHomeTeam() != -1);
        }

        // Competition filter
        if (comp != null) {
            stream = stream.filter(m -> comp.equalsIgnoreCase(m.competitionName()));
        }

        // Team filter (home OR away matches the chosen team)
        if (tm != null) {
            stream = stream.filter(m -> tm.equalsIgnoreCase(m.homeTeamName())
                    || tm.equalsIgnoreCase(m.awayTeamName()));
        }

        // Free-text search across team names
        if (q != null) {
            stream = stream.filter(m ->
                    (m.homeTeamName() != null && m.homeTeamName().toLowerCase(Locale.ROOT).contains(q))
                            || (m.awayTeamName() != null && m.awayTeamName().toLowerCase(Locale.ROOT).contains(q))
                            || (m.competitionName() != null && m.competitionName().toLowerCase(Locale.ROOT).contains(q))
            );
        }

        // Order: results = newest first, otherwise oldest first (next match on top)
        Comparator<GameDTO> byTime = "results".equals(v)
                ? Comparator.comparing(GameDTO::timeAsDateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                : Comparator.comparing(GameDTO::timeAsDateTime, Comparator.nullsLast(Comparator.naturalOrder()));

        return stream.sorted(byTime).toList();
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
