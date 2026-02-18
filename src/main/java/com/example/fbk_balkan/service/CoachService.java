package com.example.fbk_balkan.service;

//import com.example.fbk_balkan.dto.CoachCreateUpdateDTO;
import com.example.fbk_balkan.dto.CoachListItemDTO;
import com.example.fbk_balkan.dto.CoachResponseDto;
import com.example.fbk_balkan.dto.CoachCreateUpdateDTO;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.mapper.CoachMapper;
import com.example.fbk_balkan.repository.CoachRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CoachService {

    private final CoachRepository coachRepository;
    private final PasswordEncoder passwordEncoder;
    private final CoachMapper coachMapper;

    public List<CoachListItemDTO> findAllCoaches() {
        return coachRepository.findAll().stream()
                .map(coachMapper::toListItem)
                .toList();
    }

    public CoachCreateUpdateDTO getCoachForEdit(Long id) {
        Coach c = coachRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));
        CoachCreateUpdateDTO dto = new CoachCreateUpdateDTO();
        dto.setId(c.getId());
        dto.setEmail(c.getEmail());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setRole(c.getRole());
        dto.setEnabled(c.isEnabled());
        // password lämnas tom vid edit
        return dto;
    }

    public Coach createCoach(CoachCreateUpdateDTO dto) {

        if (!StringUtils.hasText(dto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lösenord krävs");
        }
        if (dto.getPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lösenord måste vara minst 8 tecken");
        }

        if (coachRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-postadressen används redan");
        }

        Coach coach = coachMapper.toEntity(dto);
        coach.setPassword(passwordEncoder.encode(dto.getPassword()));
        return coachRepository.save(coach);
    }

//    public Coach updateCoach(Long id, CoachCreateUpdateDTO dto) {
//        Coach coach = coachRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));
//
//        // Kolla unik email – men tillåt samma email för samma användare
//        if (!coach.getEmail().equalsIgnoreCase(dto.getEmail().trim()) &&
//                coachRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-postadressen används redan");
//        }
//
//        coachMapper.updateEntity(dto, coach);
//
//        // Uppdatera lösenord endast om nytt anges
//        if (StringUtils.hasText(dto.getPassword())) {
//            coach.setPassword(passwordEncoder.encode(dto.getPassword()));
//        }
//
//        return coachRepository.save(coach);
//    }

    public Coach updateCoach(Long id, CoachCreateUpdateDTO dto) {
        System.out.println("updateCoach called → ID = " + id);
        System.out.println("   New email    : " + dto.getEmail());
        System.out.println("   New firstName: " + dto.getFirstName());

        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        System.out.println("Before update → email = " + coach.getEmail() + ", firstName = " + coach.getFirstName());

        coachMapper.updateEntity(dto, coach);

        if (StringUtils.hasText(dto.getPassword())) {
            if(dto.getPassword().length() < 8){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "" +
                        "Lösenord måste vara minst 8 tecken");
            }
            coach.setPassword(passwordEncoder.encode(dto.getPassword()));
            System.out.println("Password updated");
        }

        Coach saved = coachRepository.save(coach);
        System.out.println("After save → email = " + saved.getEmail() + ", firstName = " + saved.getFirstName());

        return saved;
    }

    public void deleteCoach(Long id) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));

        if (!coach.getTeams().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Kan inte ta bort tränare som är kopplad till lag. Ta bort eller omfördela lagen först.");
        }

        coachRepository.delete(coach);
    }
}