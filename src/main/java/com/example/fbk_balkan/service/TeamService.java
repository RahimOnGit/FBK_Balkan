package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TeamCreateUpdateDTO;
import com.example.fbk_balkan.dto.team.*;
import com.example.fbk_balkan.entity.Role;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamMapper teamMapper;
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
        //NEW Validation
        if (coach.getRole() != Role.COACH) {
            throw new IllegalArgumentException("Selected user is not a head coach");
        }


//    convert DTO to entity
        Team team = teamMapper.toEntity(teamCreateDto, coach);
        team.setCreatedDate(LocalDateTime.now());
        team.setUpdatedDate(LocalDateTime.now());

        //NEW 4. Fetch and set assistant coaches (null-safe, distinct, and validate roles)
        if (teamCreateDto.getAssistantCoachIds() != null && !teamCreateDto.getAssistantCoachIds().isEmpty()) {
            List<Long> assistantIds = teamCreateDto.getAssistantCoachIds().stream()
                    .filter(id -> id != null)
                    .distinct()
                    .toList();

            //   Prevent head coach from being listed as assistant
            if (assistantIds.contains(coach.getId())) {
                throw new IllegalArgumentException("Huvudtränare kan inte vara assistenttränare i samma lag");
            }

            List<User> assistants = assistantIds.stream()
                    .map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Assistant coach not found: " + id))
                    )
                    .peek(u -> {
                        if (u.getRole() != Role.ASSISTANT_COACH) {
                            throw new IllegalArgumentException("User is not an assistant coach: " + u.getId());
                        }
                    })
                    .collect(Collectors.toList());

            team.setAssistantCoaches(assistants);
        }


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
                // Add assistant coaches names
                dto.setAssistantNames(
                        team.getAssistantCoaches().stream()
                                .map(u -> u.getFirstName() + " " + u.getLastName())
                                .collect(Collectors.joining(", "))
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
                .sorted(Comparator.comparingInt(this::sortKey))
                .toList();
    }

    private int sortKey(PublicTeamDto team) {
        String ag = team.getAgeGroup();
        if (ag == null) return 9999;

        // U19, U17 etc → first (lowest key)
        if (ag.startsWith("U")) {
            Matcher m = Pattern.compile("U(\\d+)").matcher(ag);
            if (m.find()) return 1000 - Integer.parseInt(m.group(1)); // U19=981, U17=983
        }

        // "Pojkar 2010" or "Flickor 2012" → extract year
        Matcher yearMatcher = Pattern.compile("(\\d{4})").matcher(ag);
        if (yearMatcher.find()) {
            return Integer.parseInt(yearMatcher.group(1)); // 2010, 2011, 2012...
        }

        // "Pojkar 2014/2015" → use first year
        Matcher rangeMatcher = Pattern.compile("(\\d{4})/(\\d{4})").matcher(ag);
        if (rangeMatcher.find()) {
            return Integer.parseInt(rangeMatcher.group(1));
        }

        return 9999;
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
        if (coach.getRole() != Role.COACH) {
            throw new IllegalArgumentException("Selected user is not a head coach");
        }
        // Null-safe and distinct assistant coach IDs
        List<Long> assistantIds = dto.getAssistantCoachIds() != null
                ? dto.getAssistantCoachIds().stream().distinct().toList()
                : List.of();

        // Prevent head coach from being an assistant
        if (assistantIds.contains(dto.getCoachId())) {
            throw new IllegalArgumentException("Huvudtränare kan inte vara assistenttränare i samma lag");
        }

        // Uppdatera fält
        team.setName(dto.getName());
        team.setAgeGroup(dto.getAgeGroup());
        team.setGender(Team.Gender.valueOf(dto.getGender()));
        team.setTrainingLocation(dto.getTrainingLocation());
        team.setDescription(dto.getDescription());
        team.setActive(dto.isActive());
        team.setCoach(coach);

        // Update assistant coaches
        List<User> assistants = dto.getAssistantCoachIds().stream()
                .map(assistantId -> userRepository.findById(assistantId)
                        .filter(u -> u.getRole() == Role.ASSISTANT_COACH)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid assistant coach ID: " + assistantId))
                )
                .collect(Collectors.toList());

        team.setAssistantCoaches(assistants);
        team.setUpdatedDate(LocalDateTime.now());

        teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laget är inte närvarande."));



        teamRepository.delete(team);
    }

}



