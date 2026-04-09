package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.TrialRegistration;
import com.example.fbk_balkan.entity.TrialStatus;
import com.example.fbk_balkan.enums.Gender;
import com.example.fbk_balkan.enums.ReferralSource;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialRegistrationDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 40)
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Only letters and valid name characters allowed")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 40)
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Only letters and valid name characters allowed")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Relative name is required")
    @Size(min = 2, max = 40)
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Only letters and valid name characters allowed")
    private String relativeName;

    @NotBlank(message = "Relative email is required")
    @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Size(max = 40, message = "Email cannot exceed 40 characters")
    private String relativeEmail;

    @NotBlank(message = "Relative number is required")
    @Pattern(
            regexp = "^\\+?[0-9 ]{8,15}$",
            message = "Invalid phone number")
    private String relativeNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Preferred training date is required")
    private LocalDate preferredTrainingDate;

    private TrialStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Size(max = 40, message = "Max 40 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9 .'-]*$",
            message = "Invalid characters")
    private String currentClub;

    @Min(value = 0, message = "Antal år i klubb kan inte vara negativt")
    @Max(value = 5, message = "Antal år i klubb kan inte vara mer än 5")
    private Integer clubYears;


    private ReferralSource referralSource;

    @Size(max = 50, message = "Max 50 characters")

    @Pattern(
            regexp = "^[\\p{L}0-9 .,'\"!?-]*$",
            message = "Invalid characters")
    private String referralOther;

    // The coach assigned to this registration (resolved from birth year + gender)
    // Null means no matching team was found — admin must assign manually
    private Long coachId;

    // =========================================================
    // fromEntity — used when loading registrations from the DB
    //              (e.g. for the coach dashboard list)
    // =========================================================
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
//                .createdAt(entity.getCreatedAt().atStartOfDay())
                .createdAt(entity.getCreatedAt())
                .gender(entity.getGender())
                .currentClub(entity.getCurrentClub())
                .clubYears(entity.getClubYears())
                .referralSource(entity.getReferralSource())
                .referralOther(entity.getReferralOther())
                // Populate coachId so the dashboard knows which coach owns this request
                .coachId(entity.getCoach() != null ? entity.getCoach().getId() : null)
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
//        e.setCreatedAt(LocalDate.from(this.createdAt));
        e.setCreatedAt(this.createdAt);
        e.setGender(this.gender);
        e.setCurrentClub(this.currentClub);
        e.setClubYears(this.clubYears);
        e.setReferralSource(this.referralSource);
        e.setReferralOther(
                this.referralSource == ReferralSource.OTHER ? this.referralOther : null
        );
        // Note: coach is NOT set here — the service layer handles that
        return e;
    }
}