package com.example.fbk_balkan.dto;




import jakarta.validation.constraints.*;




public class CreateChildRequest {


    @NotBlank
    @Size(max = 100)
    private String fullName;


    @NotNull
    @Min(2005)
    @Max(2020)
    private Integer birthYear;


    // Optional – display only, NOT security
    @Size(max = 50)
    private String teamCategory;


    @Size(max = 500)
    private String notes;


    @NotNull(message = "Team is required")
    private Long teamId;


    // Getters & Setters
    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public Integer getBirthYear() {
        return birthYear;
    }


    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }


    public String getTeamCategory() {
        return teamCategory;
    }


    public void setTeamCategory(String teamCategory) {
        this.teamCategory = teamCategory;
    }


    public String getNotes() {
        return notes;
    }


    public void setNotes(String notes) {
        this.notes = notes;
    }


    public Long getTeamId() {
        return teamId;
    }


    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}