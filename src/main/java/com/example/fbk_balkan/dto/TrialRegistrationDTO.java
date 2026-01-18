package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialRegistrationDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
 @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Relative name is required")
    private String relativeName;

    @NotBlank(message = "Relative email is required")
    @Email(message = "email should be valid")
    private String relativeEmail;

    @NotBlank(message = "Relative number is required")
    @Pattern(
            regexp = "^[0-9+ ]{8,15}$",
            message = " Invalid phone number"
    )
    private String relativeNumber;


    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Preferred training date is required")
    private LocalDate preferredTrainingDate;

    private TrialStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public static TrialRegistrationDTO fromEntity(TrialRegistration entity) {
        if (entity == null) return null;
        return TrialRegistrationDTO.builder()
                .id(entity.getId())
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
        e.setId(this.id);
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
