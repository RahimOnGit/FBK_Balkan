package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.PlayerDTO;
import com.example.fbk_balkan.entity.Player;
import com.example.fbk_balkan.repository.CoachRepository;
import com.example.fbk_balkan.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerDTO create(PlayerDTO playerDTO) {
        Player player = new Player();
        player.setFirstName(playerDTO.getFirstName());
        player.setLastName(playerDTO.getLastName());

        playerRepository.save(player);
        return new PlayerDTO(player.getId() , player.getFirstName(), player.getLastName());
    }


}
