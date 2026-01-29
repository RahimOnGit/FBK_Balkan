package com.example.fbk_balkan.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class PlayerDTO {

    //getters only
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String position;
    private boolean active;
    private String teamName;

    private LocalDate birthDate;


    public PlayerDTO(Long id, String firstName, String lastName, int age, String position, boolean active, String teamName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.position = position;
        this.active = active;
        this.teamName = teamName;
    }

    public PlayerDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
