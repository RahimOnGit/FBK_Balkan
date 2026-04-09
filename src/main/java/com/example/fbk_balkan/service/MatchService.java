package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.entity.Match;
import com.example.fbk_balkan.mapper.MatchMapper;
import com.example.fbk_balkan.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {
    @Autowired
private MatchRepository matchRepository;
    @Autowired
    private MatchMapper matchMapper;

public List<GameDTO> fetchMatches()
{
    List<Match> games = matchRepository.findAll();
   return games.stream()
            .map(matchMapper::toDto)
            .toList();

}
}
