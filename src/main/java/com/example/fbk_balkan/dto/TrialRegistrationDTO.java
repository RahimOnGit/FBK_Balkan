package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialRegistrationDTO {
    private Long id;

    private String firstName;

    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String relativeName;
private String relativeEmail;

    private String relativeNumber;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate preferredTrainingDate;

    private TrialStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public static TrialRegistrationDTO fromEntity(TrialRegistration entity) {
        if (entity == null) return null;
        return TrialRegistrationDTO.builder()
                .id(entity.getChildId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .relativeName(entity.getRelativeName())
                .relativeEmail(entity.getRelativeEmail())
                .relativeNumber(entity.getRelativeNumber())
                .preferredTrainingDate(entity.getPreferredTrainingDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public TrialRegistration toEntity() {
        TrialRegistration e = new TrialRegistration();
        e.setChildId(this.id);
        e.setFirstName(this.firstName);
        e.setLastName(this.lastName);
        e.setBirthDate(this.birthDate);
        e.setRelativeName(this.relativeName);
        e.setRelativeEmail(this.relativeEmail);
        e.setRelativeNumber(this.relativeNumber);
        e.setPreferredTrainingDate(this.preferredTrainingDate);
        e.setStatus(this.status);
        e.setCreatedAt(this.createdAt);
        return e;
    }
}
