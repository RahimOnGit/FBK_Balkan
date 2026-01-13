package com.example.fbk_balkan.service;




import com.example.fbk_balkan.dto.TeamSelectDTO;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.CoachTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;




import java.util.List;




@Service
@RequiredArgsConstructor
public class TeamService {


    private final CoachTeamRepository coachTeamRepository;


    public List<TeamSelectDTO> getTeamsForCoach(User coach) {
        return coachTeamRepository.findTeamsForCoach(coach.getUserId());
    }


}