package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.team.*;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamMapper teamMapper;

//    @Autowired
//    private UserRepository coachRepository;

    @Autowired
    private PublicTeamMapper publicTeamMapper;


    @Transactional
public TeamDto createTeam(TeamCreateDto teamCreateDto) {
        if (teamRepository.existsByNameAndAgeGroup(
                teamCreateDto.getName(),
                teamCreateDto.getAgeGroup()
        )) {
            throw new IllegalArgumentException("Lag finns redan för vald åldersgrupp");
        }

//validate and fetch coach
        User coach = userRepository.findById(teamCreateDto.getCoachId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid coach ID"));

//    convert DTO to entity
        Team team = teamMapper.toEntity(teamCreateDto, coach);
        team.setCreatedDate(LocalDateTime.now());
        team.setUpdatedDate(LocalDateTime.now());

//    save entity
        Team savedTeam = teamRepository.save(team);
        //  access to coach while transaction is open
        savedTeam.getCoach().getEmail();
//    convert entity to DTO and return
        return teamMapper.toDto(savedTeam);
    }
    /**
     * Fetch all teams for a specific coach
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByCoachId(Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        List<Team> teams = teamRepository.findByCoachId(coachId);

        return teams.stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
    }
//
public List<TeamListItemDTO> findAllForAdminList() {
    return teamRepository.findAll().stream()
            .map(team -> {
                TeamListItemDTO dto = new TeamListItemDTO();
                dto.setId(team.getId());
                dto.setName(team.getName());
                dto.setAgeGroup(team.getAgeGroup());
                dto.setGender(team.getGender());
                dto.setActive(team.isActive());
                dto.setCoachName(
                        team.getCoach() != null
                                ? team.getCoach().getFirstName() + " " + team.getCoach().getLastName()
                                : "—"
                );
                return dto;
            })
            .toList();
}


public List<Team> getActiveTeams() {
    return teamRepository.findByActiveTrue();
    // only active teams
}
    public List<PublicTeamDto> getSortedPublicTeams() {
        return getActiveTeams().stream()
                .map(publicTeamMapper::toDto)
                .sorted(Comparator.comparingInt((PublicTeamDto team) -> {
            try {
                String yearStr = team.getAgeGroup()
                        .replaceAll(".*\\((\\d{4})\\)", "$1");
                return Integer.parseInt(yearStr);
            } catch (Exception e) {
                return 0;
            }
        }).reversed())

                .toList();

    }
    public Team getActiveTeamById(Long id) {
        return teamRepository.findByIdAndActiveTrue(id).orElse(null);
    }



    /**
     * Fetch all teams for a specific coach by email
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByCoachEmail(String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found"));

        return getTeamsByCoachId(coach.getId());
    }

    public List<TeamListItemDTO> findLatestTeams(int limit) {
        return teamRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .limit(limit)
                .map(team -> {
                    TeamListItemDTO dto = new TeamListItemDTO();
                    dto.setId(team.getId());
                    dto.setName(team.getName());
                    dto.setAgeGroup(team.getAgeGroup());
//                    dto.setGender(team.getGender() != null ? team.getGender().name() : "—");
                    dto.setGender(team.getGender() != null ? team.getGender() : null);
                    dto.setActive(team.isActive());
                    dto.setCoachName(team.getCoach() != null
                            ? team.getCoach().getFirstName() + " " + team.getCoach().getLastName()
                            : "—");

                    dto.setGenderDisplay(switch (team.getGender()) {
                        case MALE -> "Kille";
                        case FEMALE -> "Tjej";
                        case MIXED -> "Annat";
                        default -> "Vill ej ange";
                    });
                    dto.setCreatedDate(team.getCreatedDate());

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public Team getTeamById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateTeam(Long id, TeamCreateDto dto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laget är inte närvarande."));

        User coach = userRepository.findById(dto.getCoachId())
                .orElseThrow(() -> new IllegalArgumentException("Tränaren är inte närvarande."));

        // Uppdatera fält
        team.setName(dto.getName());
        team.setAgeGroup(dto.getAgeGroup());
        team.setGender(Team.Gender.valueOf(dto.getGender()));
        team.setTrainingLocation(dto.getTrainingLocation());
        team.setDescription(dto.getDescription());
        team.setActive(dto.isActive());
        team.setCoach(coach);

        teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laget är inte närvarande."));



        teamRepository.delete(team);
    }

}



