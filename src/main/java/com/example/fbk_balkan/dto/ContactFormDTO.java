package com.example.fbk_balkan.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactFormDTO {

    @NotBlank(message = "Namn är obligatoriskt")
    @Size(min = 2, max = 80, message = "Namn måste vara 2–80 tecken")
    private String name;

    @NotBlank(message = "E-post är obligatorisk")
    @Email(message = "Ogiltig e-postadress")
    private String email;

    private String phone;

    @NotBlank(message = "Ämne är obligatoriskt")
    @Size(min = 2, max = 120, message = "Ämne måste vara 2–120 tecken")
    private String subject;

    @NotBlank(message = "Meddelande är obligatoriskt")
    @Size(min = 10, max = 2000, message = "Meddelandet måste vara 10–2000 tecken")
    private String message;
}