package com.example.fbk_balkan.dto.team;
import com.example.fbk_balkan.entity.Team;
import org.springframework.stereotype.Component;


@Component
public class PublicTeamMapper {

    public PublicTeamDto toDto(Team team) {
        if (team == null) return null;

        PublicTeamDto dto = new PublicTeamDto();
        dto.setId(team.getId());
        dto.setName(team.getName() != null ? team.getName() : "Okänt lag");
        dto.setAgeGroup(team.getAgeGroup() != null ? team.getAgeGroup() : "Okänd åldersgrupp");
        dto.setGender(team.getGender() != null ? team.getGender().name() : "Okänt");

        // Coach name: first initial + last name
        if (team.getCoach() != null) {
            String firstInitial = team.getCoach().getFirstName() != null && !team.getCoach().getFirstName().isEmpty()
                    ? team.getCoach().getFirstName().substring(0, 1) + "."
                    : "";
            String lastName = team.getCoach().getLastName() != null ? team.getCoach().getLastName() : "";
            dto.setCoachName((firstInitial + " " + lastName).trim());
        }

        dto.setTrainingLocation(team.getTrainingLocation() != null ? team.getTrainingLocation() : "Okänd plats");

        return dto;
    }
}
