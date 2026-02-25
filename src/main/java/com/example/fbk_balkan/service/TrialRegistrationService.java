package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.enums.Gender;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrialRegistrationService {

    @Autowired
    private TrialRegistrationRepository trialRegistrationRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private TeamRepository teamRepository;

    // ====
    // CREATE — saves a new trial registration and auto-assigns
    //          a coach based on the child's birth year + gender
    // ====
    public TrialRegistrationDTO create(TrialRegistrationDTO trialRegistrationDTO) {

        // --- Map DTO → Entity ---
        TrialRegistration trialRegistration = new TrialRegistration();
        trialRegistration.setFirstName(sanitize(trialRegistrationDTO.getFirstName()));
        trialRegistration.setLastName(sanitize(trialRegistrationDTO.getLastName()));
        trialRegistration.setBirthDate(trialRegistrationDTO.getBirthDate());
        trialRegistration.setRelativeName(sanitize(trialRegistrationDTO.getRelativeName()));
        trialRegistration.setRelativeEmail(sanitize(trialRegistrationDTO.getRelativeEmail()));
        trialRegistration.setRelativeNumber(sanitize(trialRegistrationDTO.getRelativeNumber()));
        trialRegistration.setPreferredTrainingDate(trialRegistrationDTO.getPreferredTrainingDate());
        trialRegistration.setGender(trialRegistrationDTO.getGender());
        trialRegistration.setCurrentClub(sanitize(trialRegistrationDTO.getCurrentClub()));
        trialRegistration.setClubYears(trialRegistrationDTO.getClubYears());
        trialRegistration.setReferralSource(trialRegistrationDTO.getReferralSource());
        trialRegistration.setReferralOther(
                trialRegistrationDTO.getReferralSource() == ReferralSource.OTHER
                        ? sanitize(trialRegistrationDTO.getReferralOther())
                        : null
        );
        trialRegistration.setStatus(com.example.fbk_balkan.entity.TrialStatus.PENDING);
        trialRegistration.setCreatedAt(LocalDate.now());


        // AUTO-ASSIGN COACH
        // Strategy:
        //   1. Extract birth year from the child's birthDate
        //   2. Map the child's gender (enums.Gender) to Team.Gender
        //   3. Search for an active team whose ageGroup contains that
        //      birth year (e.g. "P7 (2018)" contains "2018") and
        //      whose gender matches
        //   4. If found → assign that team's coach to the registration
        //   5. If not found → coach stays null (admin assigns manually)
        String birthYear = String.valueOf(trialRegistrationDTO.getBirthDate().getYear());
        Team.Gender teamGender = mapToTeamGender(trialRegistrationDTO.getGender());

        List<Team> matchingTeams = teamRepository.findActiveTeamsByBirthYearAndGender(birthYear, teamGender);

        if (matchingTeams.isEmpty() && teamGender == Team.Gender.MIXED) {
            // Fallback for MIXED: search by year only
            matchingTeams = teamRepository.findActiveTeamsByBirthYear(birthYear);
        }

        if (!matchingTeams.isEmpty()) {
            // Take the first matching team's coach
            // (If multiple teams same year/gender exist, first active one wins)
            User assignedCoach = matchingTeams.get(0).getCoach();
            trialRegistration.setCoach(assignedCoach);
        }
        // If still no match → coach remains null → admin assigns later

        // --- Save ---
        trialRegistrationRepository.save(trialRegistration);

        // --- Build and return DTO ---
        return TrialRegistrationDTO.builder()
                .id(trialRegistration.getId())
                .firstName(trialRegistration.getFirstName())
                .lastName(trialRegistration.getLastName())
                .birthDate(trialRegistration.getBirthDate())
                .relativeName(trialRegistration.getRelativeName())
                .relativeEmail(trialRegistration.getRelativeEmail())
                .relativeNumber(trialRegistration.getRelativeNumber())
                .preferredTrainingDate(trialRegistration.getPreferredTrainingDate())
                .gender(trialRegistration.getGender())
                .currentClub(trialRegistration.getCurrentClub())
                .clubYears(trialRegistration.getClubYears())
                .referralSource(trialRegistration.getReferralSource())
                .referralOther(trialRegistration.getReferralOther())
                .status(trialRegistration.getStatus())
                .createdAt(trialRegistration.getCreatedAt().atStartOfDay())
                .coachId(trialRegistration.getCoach() != null ? trialRegistration.getCoach().getId() : null)
                .build();
    }

    // ==
    // FETCH — returns all trial registrations for a given coach
    //         ordered newest first
    // ===
    public List<TrialRegistrationDTO> fetchTrialRegistrationByCoach(Long coachId) {
        coachRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found with id: " + coachId));

        return trialRegistrationRepository.findByCoachIdOrderByCreatedAtDesc(coachId)
                .stream()
                .map(TrialRegistrationDTO::fromEntity)
                .toList();
    }

    // =========================================================
    private Team.Gender mapToTeamGender(Gender gender) {
        if (gender == null) return Team.Gender.MIXED;
        return switch (gender) {
            case MALE, ANNAT, VillEjAnge , FEMALE -> Team.Gender.MALE;

            //change this if they made a female team right now there is no
//            case FEMALE -> Team.Gender.FEMALE;
        };
    }

    // HELPER — removes HTML tags and trims whitespace
    private String sanitize(String value) {
        if (value == null) return null;
        return value.trim().replaceAll("<.*?>", "");
    }
}