package com.example.fbk_balkan.dto.svff;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)

public class SvffClubResponse {
    private List<SvffTeamDto> teams;
    private List<GameDTO> games;


}
