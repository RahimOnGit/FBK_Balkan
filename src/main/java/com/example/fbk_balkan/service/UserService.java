package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.ChangePasswordDto;
import com.example.fbk_balkan.dto.UserCreateUpdateDto;
import com.example.fbk_balkan.dto.UserListItemDTO;
import com.example.fbk_balkan.dto.UserProfileViewDto;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.mapper.UserMapper;          // ny mapper – skapa denna
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import com.example.fbk_balkan.security.CustomUserDetails;


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
        dto.setPhone(user.getPhone()); // NEW
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
        user.setPhone(dto.getPhone());// NEW
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
                    // New
                    dto.setPhone(user.getPhone());
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

//    public UserProfileViewDto getCurrentUserProfile(String email) {
public UserProfileViewDto getCurrentUserProfile(Long userId) {
//        User user = userRepository.findByEmail(email)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileViewDto dto = new UserProfileViewDto();

        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        // SAFE ROLE MAPPING (real-world approach)
        dto.setRoleLabel(mapRoleToLabel(user.getRole()));

        return dto;
    }
    @Transactional
    public void updatePhone(Long userId, String phone) {
//        User user = userRepository.findByEmail(email)
        User user = userRepository.findById(userId)

                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPhone(
                phone != null && !phone.isBlank()
                        ? phone.trim()
                        : null
        );
    }
    private String mapRoleToLabel(Role role) {
        if (role == null) return "—";

        return switch (role) {
            case ADMIN -> "Admin";
            case COACH -> "Tränare";
            case ASSISTANT_COACH -> "Assisterande tränare";
            case SOCIAL_ADMIN -> "Social Admin";
        };
    }
    @Transactional
    public void changePassword(Long userId, ChangePasswordDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //Wrong current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Nuvarande lösenord är fel");
        }

        // New passwords don't match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Lösenorden matchar inte");
        }

        // Prevent reuse
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Du kan inte återanvända ditt gamla lösenord");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

}