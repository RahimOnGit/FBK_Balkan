package com.example.fbk_balkan.mapper;

//import com.example.fbk_balkan.dto.CoachCreateUpdateDTO;
import com.example.fbk_balkan.dto.UserListItemDTO;
import com.example.fbk_balkan.dto.UserCreateUpdateDto;
import com.example.fbk_balkan.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserListItemDTO toListItem(User user) {
        UserListItemDTO dto = new UserListItemDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFirstName() + " " + user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        dto.setTeamCount(user.getTeams() != null ? user.getTeams().size() : 0);
        dto.setPhone(user.getPhone());
        return dto;
    }

    public User toEntity(UserCreateUpdateDto dto) {
        User u = new User();
        u.setEmail(dto.getEmail().trim().toLowerCase());
        u.setFirstName(dto.getFirstName().trim());
        u.setLastName(dto.getLastName().trim());
        u.setRole(dto.getRole());
        u.setEnabled(dto.isEnabled());
        // NEW
        u.setPhone(
                dto.getPhone() != null && !dto.getPhone().isBlank()
                        ? dto.getPhone().trim()
                        : null
        );
        return u;
    }

    public void updateEntity(UserCreateUpdateDto dto, User entity) {
        entity.setEmail(dto.getEmail().trim().toLowerCase());
        entity.setFirstName(dto.getFirstName().trim());
        entity.setLastName(dto.getLastName().trim());
        entity.setRole(dto.getRole());
        entity.setEnabled(dto.isEnabled());
        // lösenord hanteras separat i service
        // Safe phone handling
        entity.setPhone(
                dto.getPhone() != null && !dto.getPhone().isBlank()
                        ? dto.getPhone().trim()
                        : null
        );
    }
}