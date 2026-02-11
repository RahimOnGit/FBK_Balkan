package com.example.fbk_balkan.mapper;

//import com.example.fbk_balkan.dto.CoachCreateUpdateDTO;
import com.example.fbk_balkan.dto.CoachListItemDTO;
import com.example.fbk_balkan.dto.CoachCreateUpdateDTO;
import com.example.fbk_balkan.entity.Coach;
import org.springframework.stereotype.Component;

@Component
public class CoachMapper {

    public CoachListItemDTO toListItem(Coach c) {
        CoachListItemDTO dto = new CoachListItemDTO();
        dto.setId(c.getId());
        dto.setFullName(c.getFirstName() + " " + c.getLastName());
        dto.setEmail(c.getEmail());
        dto.setRole(c.getRole());
        dto.setEnabled(c.isEnabled());
        dto.setTeamCount(c.getTeams() != null ? c.getTeams().size() : 0);
        return dto;
    }

    public Coach toEntity(CoachCreateUpdateDTO dto) {
        Coach c = new Coach();
        c.setEmail(dto.getEmail().trim().toLowerCase());
        c.setFirstName(dto.getFirstName().trim());
        c.setLastName(dto.getLastName().trim());
        c.setRole(dto.getRole());
        c.setEnabled(dto.isEnabled());
        // Lösenord sätts i service-lagret
        return c;
    }

    public void updateEntity(CoachCreateUpdateDTO dto, Coach entity) {
        entity.setEmail(dto.getEmail().trim().toLowerCase());
        entity.setFirstName(dto.getFirstName().trim());
        entity.setLastName(dto.getLastName().trim());
        entity.setRole(dto.getRole());
        entity.setEnabled(dto.isEnabled());
        // Lösenord uppdateras bara om nytt lösenord skickas (hanteras i service)
    }
}