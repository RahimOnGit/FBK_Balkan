package com.example.fbk_balkan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "match_details")
@Getter
@Setter
public class MatchDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long gameNumber;

    private String competitionCategoryName;
    @Column(length = 50)
    private String result;

    private String gameHomeTeamFormation;
    private String gameAwayTeamFormation;

    private String refereeName;

    private String assistant1Name;

    private String ageCategoryName;
}