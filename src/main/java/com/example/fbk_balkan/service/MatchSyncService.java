package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.entity.Match;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.mapper.MatchMapper;
import com.example.fbk_balkan.repository.MatchRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.service.external.SvffApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSyncService {

    private final SvffApiService svffApiService;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final MatchMapper matchMapper;


//      Runs every 6 hours.
//            *
//            * Change to "0 0 * * * *" for every hour,
    @Scheduled(cron = "0 0 */6 * * *") //      "0 0 */6 * * *" = at minute 0, every 6th hour
    @CacheEvict(value = "svffGames", allEntries = true)  // clears the cache before fetching
    @Transactional
    public void syncGames() {
        log.info("Starting scheduled match sync...");
        try {
            List<GameDTO> games = svffApiService.fetchGames();
            int created = 0;
            int updated = 0;

            for (GameDTO dto : games) {
                String resolvedHome = resolveTeamName(dto.homeTeamId(), dto.homeTeamName());
                String resolvedAway = resolveTeamName(dto.awayTeamId(), dto.awayTeamName());

                var existing = matchRepository.findByGameNumber(dto.gameNumber());

                if (existing.isPresent()) {
                    updateMatch(existing.get(), dto, resolvedHome, resolvedAway);
                    updated++;
                } else {
                    Match match = matchMapper.toEntity(dto);
                    match.setHomeTeamName(resolvedHome);
                    match.setAwayTeamName(resolvedAway);
                    matchRepository.save(match);
                    created++;
                }
            }
            log.info("Match sync done. Created: {}, Updated: {}", created, updated);
        } catch (Exception e) {
            log.error("Scheduled match sync failed: {}", e.getMessage());
        }
    }

    private void updateMatch(Match existing, GameDTO dto,
                             String resolvedHome, String resolvedAway) {
        boolean changed = false;

        if (existing.getHomeTeamSvffId() == null && dto.homeTeamId() != null) {
            existing.setHomeTeamSvffId(dto.homeTeamId());
            existing.setHomeTeamName(resolvedHome);
            changed = true;
        }
        if (existing.getAwayTeamSvffId() == null && dto.awayTeamId() != null) {
            existing.setAwayTeamSvffId(dto.awayTeamId());
            existing.setAwayTeamName(resolvedAway);
            changed = true;
        }
        if (dto.isFinished() != null) {
            existing.setIsFinished(dto.isFinished());
            changed = true;
        }
        if (dto.result() != null) {
            existing.setResult(dto.result());
            changed = true;
        }
        if (dto.refereeName() != null) {
            existing.setRefereeName(dto.refereeName());
            changed = true;
        }
        if (dto.competitionCategoryName() != null) {
            existing.setCompetitionCategoryName(dto.competitionCategoryName());
            changed = true;
        }
        if (dto.ageCategoryName() != null) {
            existing.setAgeCategoryName(dto.ageCategoryName());
            changed = true;
        }
        if (dto.goalsScoredHomeTeam() != null) {
            existing.setGoalsScoredHomeTeam(dto.goalsScoredHomeTeam());
            changed = true;
        }
        if (dto.goalsScoredAwayTeam() != null) {
            existing.setGoalsScoredAwayTeam(dto.goalsScoredAwayTeam());
            changed = true;
        }

        if (changed) matchRepository.save(existing);
    }

    private String resolveTeamName(Long svffTeamId, String fallbackName) {
        if (svffTeamId == null) return fallbackName;
        return teamRepository.findBySvffTeamId(svffTeamId)
                .map(Team::getName)
                .orElse(fallbackName);
    }
}