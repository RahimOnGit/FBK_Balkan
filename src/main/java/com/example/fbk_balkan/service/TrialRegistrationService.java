package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.repository.CoachRepository;
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
    private  TrialRegistrationRepository trialRegistrationRepository;
    @Autowired
    private CoachRepository coachRepository;

    //    create method
    public TrialRegistrationDTO create(TrialRegistrationDTO trialRegistrationDTO) {
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
        trialRegistration.setCreatedAt(LocalDate.from(LocalDateTime.now()));

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



        // Save entity
        //trialRegistrationRepository.save(trialRegistration);

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
                .createdAt(trialRegistration.getCreatedAt().atStartOfDay())
                .build();

    }

    public List<TrialRegistrationDTO> fetchTrialRegistrationByCoach(Long coachId) {
//convert repo into service
        Coach coach = coachRepository.findById(coachId).
                orElseThrow(()-> new IllegalArgumentException("coach not found"));

        return trialRegistrationRepository.findByCoachIdOrderByCreatedAtDesc(coachId)
                .stream()
                .map(TrialRegistrationDTO::fromEntity)
                .toList();


    }


// Sanitization helper
private String sanitize(String value) {
    if (value == null) return null;
    return value.trim().replaceAll("<.*?>", ""); // remove HTML tags
}}