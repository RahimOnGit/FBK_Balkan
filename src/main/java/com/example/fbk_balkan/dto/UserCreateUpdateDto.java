package com.example.fbk_balkan.dto;

import com.example.fbk_balkan.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateUpdateDto {

    private Long id;

    @NotBlank(message = "E-postadress krävs")
    @Email(message = "Ogiltig e-postadress")
    private String email;

    @NotBlank(message = "Förnamn krävs")
    @Size(min = 2, max = 50, message = "Förnamn måste vara mellan 2 och 50 tecken")
    private String firstName;

    @NotBlank(message = "Efternamn krävs")
    @Size(min = 2, max = 50, message = "Efternamn måste vara mellan 2 och 50 tecken")
    private String lastName;

//    @NotBlank(message = "Lösenord krävs")
//    @Size(min = 8, message = "Lösenord måste vara minst 8 tecken")
//    @ConditionalOnNotEmpty
    private String password;

    @Size(max = 20, message = "Telefonnummer är för långt")
    private String phone;

    @NotNull(message = "Roll krävs")
//    private Role role;
    private Role role = Role.COACH;

    private boolean enabled = true;
}
