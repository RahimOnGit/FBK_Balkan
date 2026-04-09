package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.enums.SponsorCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SponsorDTO {

    private Long id;

    @NotBlank(message = "Namn krävs")
    private String name;

    private String logoUrl;
    private String websiteUrl;
    private String description;

    @NotNull(message = "Kategori krävs")
    private SponsorCategory category;

    private String contactName;
    private String contactEmail;
    private String contactPhone;

    private LocalDate agreementStart;
    private LocalDate agreementEnd;

    private Integer amountSek;

    private boolean active = true;
}
