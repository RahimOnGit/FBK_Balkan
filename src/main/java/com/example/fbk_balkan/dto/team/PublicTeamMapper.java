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
dto.setDescription(team.getDescription()!=null? team.getDescription(): "FBK Balkan är en klubb där ambition möter tradition, och där varje spelare får chansen att växa – både på planen och som människa. Vi är stolta över vår historia, men ännu mer taggade på framtiden.\n" +
        "\n" +
        " \n" +
        "\n" +
        "Hos oss formas talanger, byggs vänskaper och skapas en gemenskap som sträcker sig långt bortom 90 minuter fotboll. Oavsett om du drömmer om att bli proffs, vill träna för glädjens skull eller bara heja från sidlinjen – här finns en plats för dig.\n" +
        "\n" +
        " \n" +
        "\n" +
        "Följ med oss på resan. Från Malmö till resten av världen.\n" +
        "\n" +
        "FBK Balkan – Där drömmar tar fart.\n" +
        "\n");
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
