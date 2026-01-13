package com.example.fbk_balkan.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;




public class UpdateChildRequest {


    @NotBlank
    @Size(max = 100)
    private String fullName;


    @NotNull
    @Min(2005)
    @Max(2025)
    private Integer birthYear;


    @Size(max = 50)
    private String teamCategory;


    @Size(max = 500)
    private String notes;




    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }


    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }


    public String getTeamCategory() { return teamCategory; }
    public void setTeamCategory(String teamCategory) { this.teamCategory = teamCategory; }


    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }


}