package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.enums.ReferralSource;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TrialRegistrationService {

    @Autowired
    private  TrialRegistrationRepository trialRegistrationRepository;

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
                        ? referralOther // senetize
                        : null
        );

        trialRegistration.setStatus(com.example.fbk_balkan.entity.TrialStatus.PENDING);
        trialRegistration.setCreatedAt(LocalDate.from(LocalDateTime.now()));

        // Save entity
        trialRegistrationRepository.save(trialRegistration);

        // used here  builder methode to add new fields any time easily.
        //  Return DTO with sanitized values
        return TrialRegistrationDTO.builder()
                .id(trialRegistration.getId())
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(trialRegistration.getBirthDate())
                .relativeName(relativeName)
                .relativeEmail(relativeEmail)
                .relativeNumber(relativeNumber)
                .preferredTrainingDate(trialRegistration.getPreferredTrainingDate())
                .gender(trialRegistration.getGender())
                .currentClub(currentClub)
                .clubYears(trialRegistration.getClubYears())
                .referralSource(trialRegistration.getReferralSource())
                .referralOther(trialRegistration.getReferralOther())
                .status(trialRegistration.getStatus())
                .createdAt(trialRegistration.getCreatedAt().atStartOfDay())
                .build();
    }

// Sanitization helper
private String sanitize(String value) {
    if (value == null) return null;
    return value.trim().replaceAll("<.*?>", ""); // remove HTML tags
}}