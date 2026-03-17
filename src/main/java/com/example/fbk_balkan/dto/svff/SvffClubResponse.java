package com.example.fbk_balkan.dto.svff;

import java.util.List;

public class SvffClubResponse {
    private List<SvffTeamDto> teams;

    public List<SvffTeamDto> getTeams()
    {return teams;}

    public void setTeams(List<SvffTeamDto> teams) {
        this.teams = teams;
    }
}
