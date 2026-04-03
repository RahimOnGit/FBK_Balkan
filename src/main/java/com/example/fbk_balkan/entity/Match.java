package com.example.fbk_balkan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
  private   Long gameId;

  private  Long gameNumber;
   private String homeTeamName;
   private String awayTeamName;
   private String homeTeamImageUrl;
   private String awayTeamImageUrl;

   private Integer goalsScoredHomeTeam;
   private Integer goalsScoredAwayTeam;

   private String competitionName;
   private String seasonName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timeAsDateTime;


}
