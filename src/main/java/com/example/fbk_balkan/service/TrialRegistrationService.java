package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.TrialRegistrationDTO;
import com.example.fbk_balkan.repository.TrialRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrialRegistrationService {

  @Autowired
    private  TrialRegistrationRepository trialRegistrationRepository;

//    create method
    public TrialRegistrationDTO create(TrialRegistrationDTO trialRegistrationDTO) {
        // Map DTO to entity
        var trialRegistration = new com.example.fbk_balkan.entity.TrialRegistration();
        trialRegistration.setFirstName(trialRegistrationDTO.getFirstName());
        trialRegistration.setLastName(trialRegistrationDTO.getLastName());
        trialRegistration.setBirthDate(trialRegistrationDTO.getBirthDate());
        trialRegistration.setRelativeName(trialRegistrationDTO.getRelativeName());
        trialRegistration.setRelativeEmail(trialRegistrationDTO.getRelativeEmail());
        trialRegistration.setRelativeNumber(trialRegistrationDTO.getRelativeNumber());
        trialRegistration.setPreferredTrainingDate(trialRegistrationDTO.getPreferredTrainingDate());
        trialRegistration.setGender(trialRegistrationDTO.getGender());          // NEW
        trialRegistration.setCurrentClub(trialRegistrationDTO.getCurrentClub()); // NEW
        trialRegistration.setClubYears(trialRegistrationDTO.getClubYears());     // NEW
        trialRegistration.setStatus(com.example.fbk_balkan.entity.TrialStatus.PENDING);
        trialRegistration.setCreatedAt(LocalDate.now());

        // Save entity
        trialRegistrationRepository.save(trialRegistration);

        // Map entity back to DTO
//        return new TrialRegistrationDTO(
//                trialRegistration.getId(),
//                trialRegistration.getFirstName(),
//                trialRegistration.getLastName(),
//                trialRegistration.getBirthDate(),
//                trialRegistration.getRelativeName(),
//                trialRegistration.getRelativeEmail(),
//                trialRegistration.getRelativeNumber(),
//                trialRegistration.getPreferredTrainingDate(),
//                trialRegistration.getGender(),       // NEW
//                trialRegistration.getCurrentClub(),  // NEW
//                trialRegistration.getClubYears(),
//                trialRegistration.getStatus(),
//                trialRegistration.getCreatedAt()
//        );
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
                .status(trialRegistration.getStatus())
                .createdAt(trialRegistration.getCreatedAt())
                .build();

    }



}
