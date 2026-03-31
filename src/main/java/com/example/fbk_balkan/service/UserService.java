package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.UserCreateUpdateDto;
import com.example.fbk_balkan.dto.UserListItemDTO;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.mapper.UserMapper;          // ny mapper – skapa denna
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TeamRepository teamRepository;

    /**
     * Hämta alla användare som lista för admin-vyn
     */
    public List<UserListItemDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toListItem)
                .toList();
    }

    /**
     * Hämta en användare för redigeringsformulär (DTO)
     */
    public UserCreateUpdateDto getUserForEdit(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Användare hittades inte"));

        UserCreateUpdateDto dto = new UserCreateUpdateDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        // password lämnas tom vid redigering
        return dto;
    }

    /**
     * Skapa ny användare (tränare eller admin)
     */

    public User createUser(UserCreateUpdateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User user = new User();
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setEnabled(dto.isEnabled());
        return userRepository.save(user);
    }

    /**
     * Uppdatera befintlig användare
     */
    public User updateUser(Long id, UserCreateUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Användare hittades inte"));

        // Kolla unik e-post – tillåt samma e-post för samma användare
        String newEmail = dto.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail) &&
                userRepository.existsByEmail(newEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "E-postadressen används redan");
        }

        // Uppdatera fält (via mapper)
        userMapper.updateEntity(dto, user);

        // Uppdatera lösenord endast om nytt anges
        if (StringUtils.hasText(dto.getPassword())) {
            if (dto.getPassword().length() < 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Lösenord måste vara minst 8 tecken");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * Ta bort användare (med kontroll att inga lag är kopplade)
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Användare hittades inte"));

        if (!user.getTeams().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Kan inte ta bort användare som är huvudtränare för lag. " +
                            "Ta bort eller omfördela lagen först.");
        }

        userRepository.delete(user);
    }

    // ────────────────────────────────────────────────
    // Hjälpmetoder som kan behövas i andra delar av systemet
    // ────────────────────────────────────────────────

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Användare med e-post " + email + " hittades inte"));
    }

    public List<UserListItemDTO> findAllForAdminList() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserListItemDTO dto = new UserListItemDTO();
                    dto.setId(user.getId());
                    dto.setFullName(user.getFirstName() + " " + user.getLastName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole());
//                    dto.setTeamCount(user.getTeams() != null ? user.getTeams().size() : 0);
                    // Count main teams (as head coach)
                    int headCoachTeams = user.getTeams().size();
                    int assistantTeams = user.getAssistantTeams().size();
                    // Combine both counts into teamCount
                    dto.setTeamCount(headCoachTeams + (int) assistantTeams);
                    dto.setEnabled(user.isEnabled());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<User> findAllCoaches() {
        return userRepository.findByRole(Role.COACH);
    }

    public List<User> findAllAdmins() {
        return userRepository.findByRole(Role.ADMIN);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }
}