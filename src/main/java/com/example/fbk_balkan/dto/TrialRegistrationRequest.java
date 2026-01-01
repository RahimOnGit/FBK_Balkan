package com.example.fbk_balkan.dto;

import jakarta.validation.constraints.*;

public class TrialRegistrationRequest {

    @NotBlank
    private String childName;

    @NotNull
    @Min(2005)
    @Max(2022)
    private Integer birthYear;

    @NotBlank
    private String gender;

    private String currentClub;

    @Min(0)
    @Max(15)
    private Integer yearsInCurrentClub;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String teamCategory;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrentClub() {
        return currentClub;
    }

    public void setCurrentClub(String currentClub) {
        this.currentClub = currentClub;
    }

    public Integer getYearsInCurrentClub() {
        return yearsInCurrentClub;
    }

    public void setYearsInCurrentClub(Integer yearsInCurrentClub) {
        this.yearsInCurrentClub = yearsInCurrentClub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTeamCategory() {
        return teamCategory;
    }

    public void setTeamCategory(String teamCategory) {
        this.teamCategory = teamCategory;
    }
}




