package com.example.fbk_balkan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FaqFormDTO {

    private Long id;

    @NotBlank(message = "Frågan får inte vara tom")
    @Size(max = 255)
    private String question;

    @NotBlank(message = "Svaret får inte vara tomt")
    @Size(max = 2000)
    private String answer;

    private Integer displayOrder;

    private Boolean visible = true;
}