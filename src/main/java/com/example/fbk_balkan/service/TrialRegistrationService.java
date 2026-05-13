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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${trial.registration.duplicate-window-days:30}")
    private int duplicateWindowDays;

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

        // Max 60 days ahead
        if (date.isAfter(LocalDate.now().plusDays(60))) {
            throw new IllegalArgumentException("Du kan bara boka upp till 60 dagar framåt.");
        }

        // Sanitize string fields
        String firstName = sanitize(trialRegistrationDTO.getFirstName());
        String lastName = sanitize(trialRegistrationDTO.getLastName());
        String relativeName = sanitize(trialRegistrationDTO.getRelativeName());
        String relativeEmail = sanitize(trialRegistrationDTO.getRelativeEmail());
        String relativeNumber = sanitize(trialRegistrationDTO.getRelativeNumber());
        String currentClub = sanitize(trialRegistrationDTO.getCurrentClub());
        String referralOther = sanitize(trialRegistrationDTO.getReferralOther());

        // Normalize for duplicate checking
        String normalizedFirstName = normalize(firstName);
        String normalizedLastName = normalize(lastName);
        String normalizedEmail = normalize(relativeEmail);

        // Check for duplicates: Same Email + First Name
        boolean duplicateEmailFirstName = trialRegistrationRepository
                .existsByRelativeEmailIgnoreCaseAndFirstNameIgnoreCase(
                        normalizedEmail,
                        normalizedFirstName
                );

        // Check for duplicates: Same child on the same training date
        boolean duplicateSameTraining = trialRegistrationRepository
                .existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDateAndPreferredTrainingDate(
                        normalizedFirstName,
                        normalizedLastName,
                        trialRegistrationDTO.getBirthDate(),
                        trialRegistrationDTO.getPreferredTrainingDate()
                );

        if (duplicateEmailFirstName) {
            throw new IllegalStateException("Ett barn med detta förnamn är redan registrerat för denna e-postadress.");
        }

        if (duplicateSameTraining) {
            throw new IllegalStateException("Barnet är redan registrerat för detta provträningstillfälle.");
        }

        // Map DTO to entity
        TrialRegistration trialRegistration = new TrialRegistration();
        trialRegistration.setFirstName(firstName);
        trialRegistration.setLastName(lastName);
        trialRegistration.setBirthDate(trialRegistrationDTO.getBirthDate());
        trialRegistration.setRelativeName(relativeName);
        trialRegistration.setRelativeEmail(relativeEmail);
        trialRegistration.setRelativeNumber(relativeNumber);
        trialRegistration.setPreferredTrainingDate(trialRegistrationDTO.getPreferredTrainingDate());
        trialRegistration.setGender(trialRegistrationDTO.getGender());
        trialRegistration.setCurrentClub(currentClub);
        trialRegistration.setClubYears(trialRegistrationDTO.getClubYears());
        trialRegistration.setReferralSource(trialRegistrationDTO.getReferralSource());
        trialRegistration.setCreatedAt(LocalDateTime.now());
        trialRegistration.setStatus(com.example.fbk_balkan.entity.TrialStatus.PENDING);

        trialRegistration.setReferralOther(
                trialRegistrationDTO.getReferralSource() == ReferralSource.OTHER
                        ? referralOther
                        : null
        );

        // Coach assignment logic
        String birthYear = String.valueOf(trialRegistrationDTO.getBirthDate().getYear());
        Team.Gender teamGender = mapToTeamGender(trialRegistrationDTO.getGender());

        List<Team> matchingTeams = teamRepository.findActiveTeamsByBirthYearAndGender(birthYear, teamGender);

        if (matchingTeams.isEmpty() && teamGender == Team.Gender.MIXED) {
            matchingTeams = teamRepository.findActiveTeamsByBirthYear(birthYear);
        }

        if (!matchingTeams.isEmpty()) {
            User assignedCoach = matchingTeams.get(0).getCoach();
            trialRegistration.setCoach(assignedCoach);
        }

        // Save entity with safety for race conditions
        try {
            trialRegistrationRepository.save(trialRegistration);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Barnet är redan registrerat för detta provträningstillfälle.");
        }

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
                .coachId(trialRegistration.getCoach() != null ? trialRegistration.getCoach().getId() : null)
                .build();
    }

    // ===
    // FETCH — returns all PENDING registrations plus
    //         APPROVED/REJECTED from the last 3 months
    // ===
    public List<TrialRegistrationDTO> fetchTrialRegistrationByCoach(Long coachId) {
        userRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found with id: " + coachId));

        LocalDateTime cutoff = LocalDateTime.now().minusMonths(3);

        return trialRegistrationRepository.findActiveAndRecentByCoachId(coachId, cutoff)
                .stream()
                .map(TrialRegistrationDTO::fromEntity)
                .toList();
    }

    // =========================================================

    private Team.Gender mapToTeamGender(Gender gender) {
        if (gender == null) return Team.Gender.MIXED;
        return switch (gender) {
            case MALE, ANNAT, VillEjAnge, FEMALE -> Team.Gender.MALE;
            // Note: If female teams are added, uncomment logic here.
        };
    }

    private String sanitize(String value) {
        if (value == null) return null;
        return value.trim().replaceAll("<.*?>", ""); // remove HTML tags
    }

    private String normalize(String value) {
        if (value == null) return null;
        return value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }
}
