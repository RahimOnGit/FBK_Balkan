package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.entity.Team;
import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.enums.Gender;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrialRegistrationService {

    @Autowired
    private TrialRegistrationRepository trialRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    // ====
    // CREATE — saves a new trial registration and auto-assigns
    //          a coach based on the child's birth year + gender
    // ====
    public TrialRegistrationDTO create(TrialRegistrationDTO trialRegistrationDTO) {
        LocalDate date = trialRegistrationDTO.getPreferredTrainingDate();

        if (date == null) {
            throw new IllegalArgumentException("Välj ett datum.");
        }

        // No past dates
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Datum kan inte vara i det förflutna.");
        }

        //  Max 60 days ahead
        if (date.isAfter(LocalDate.now().plusDays(60))) {
            throw new IllegalArgumentException("Du kan bara boka upp till 60 dagar framåt.");
        }

        // Sanitize all string fields
        String firstName = sanitize(trialRegistrationDTO.getFirstName());
        String lastName = sanitize(trialRegistrationDTO.getLastName());
        String relativeName = sanitize(trialRegistrationDTO.getRelativeName());
        String relativeEmail = sanitize(trialRegistrationDTO.getRelativeEmail());
        String relativeNumber = sanitize(trialRegistrationDTO.getRelativeNumber());
        String currentClub = sanitize(trialRegistrationDTO.getCurrentClub());
        String referralOther = sanitize(trialRegistrationDTO.getReferralOther());
        // Map DTO to entity
        var trialRegistration = new com.example.fbk_balkan.entity.TrialRegistration();
        trialRegistration.setFirstName(firstName);
        trialRegistration.setLastName(lastName);
        trialRegistration.setBirthDate(trialRegistrationDTO.getBirthDate());
        trialRegistration.setRelativeName(relativeName);
        trialRegistration.setRelativeEmail(relativeEmail);
        trialRegistration.setRelativeNumber(relativeNumber);
        trialRegistration.setPreferredTrainingDate(trialRegistrationDTO.getPreferredTrainingDate());
        trialRegistration.setGender(trialRegistrationDTO.getGender());          // NEW
        trialRegistration.setCurrentClub(currentClub); // NEW
        trialRegistration.setClubYears(trialRegistrationDTO.getClubYears());     // NEW
        trialRegistration.setReferralSource(trialRegistrationDTO.getReferralSource());// NEW

        trialRegistration.setReferralOther(
                trialRegistrationDTO.getReferralSource() == ReferralSource.OTHER
                        ? referralOther
                        : null
        );

        trialRegistration.setStatus(com.example.fbk_balkan.entity.TrialStatus.PENDING);
//        trialRegistration.setCreatedAt(LocalDate.from(LocalDateTime.now()));
        // Duplicate check
        boolean exists = trialRegistrationRepository
                .existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDateAndPreferredTrainingDate(
                        firstName,
                        lastName,
                        trialRegistrationDTO.getBirthDate(),
                        trialRegistrationDTO.getPreferredTrainingDate()
                );

        if (exists) {
            throw new IllegalStateException("Barnet är redan registrerat för detta provträningstillfälle.");
        }
        try {
            trialRegistrationRepository.save(trialRegistration);
            //Handles race conditions
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Barnet är redan registrerat för detta provträningstillfälle.");
        }
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

        // Save entity
        trialRegistrationRepository.save(trialRegistration);

        // used here  builder methode to add new fields any time easily.
        return TrialRegistrationDTO.builder()
                .id(trialRegistration.getId())
                .firstName(trialRegistration.getFirstName())
                .lastName(trialRegistration.getLastName())
                .birthDate(trialRegistration.getBirthDate())
                .relativeName(trialRegistration.getRelativeName())
                .relativeEmail(trialRegistration.getRelativeEmail())
                .relativeNumber(trialRegistration.getRelativeNumber())
                .preferredTrainingDate(trialRegistration.getPreferredTrainingDate())
                .gender(trialRegistration.getGender())          // NEW
                .currentClub(trialRegistration.getCurrentClub()) // NEW
                .clubYears(trialRegistration.getClubYears())    // NEW
                .referralSource(trialRegistration.getReferralSource())  // NEW
                .referralOther(trialRegistration.getReferralOther())// NEW
                .status(trialRegistration.getStatus())
//                .createdAt(trialRegistration.getCreatedAt().atStartOfDay())
                .coachId(trialRegistration.getCoach() != null ? trialRegistration.getCoach().getId() : null)
                .build();

    }

    // ==
    // FETCH — returns all trial registrations for a given coach
    //         ordered newest first
    // ===
    public List<TrialRegistrationDTO> fetchTrialRegistrationByCoach(Long coachId) {
        userRepository.findById(coachId)
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
            case MALE, ANNAT, VillEjAnge, FEMALE -> Team.Gender.MALE;

            //change this if they made a female team right now there is no
//            case FEMALE -> Team.Gender.FEMALE;
        };
    }



// Sanitization helper
private String sanitize(String value) {
    if (value == null) return null;
    return value.trim().replaceAll("<.*?>", ""); // remove HTML tags
}}