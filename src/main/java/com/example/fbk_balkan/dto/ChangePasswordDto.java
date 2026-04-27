package com.example.fbk_balkan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {

    @NotBlank(message = "Nuvarande lösenord krävs")
    private String currentPassword;

    @NotBlank(message = "Nytt lösenord krävs")
    @Size(min = 8, message = "Lösenord måste vara minst 8 tecken")
    private String newPassword;

    @NotBlank(message = "Bekräfta lösenord krävs")
    private String confirmPassword;
}