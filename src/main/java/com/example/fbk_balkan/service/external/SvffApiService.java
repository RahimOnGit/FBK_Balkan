package com.example.fbk_balkan.service.external;

import com.example.fbk_balkan.dto.match.GameDTO;
import com.example.fbk_balkan.dto.svff.SvffClubResponse;
import com.example.fbk_balkan.dto.svff.SvffTeamDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Service
public class SvffApiService {
    @Value("${svff.api.key}")
    private String apiKey;

    @Value("${svff.api.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    @Cacheable("svffGames")
    public List<GameDTO> fetchGames()
    {
        String url = baseUrl + "/club/upcoming-games?from=2026-03-01&to=2026-03-10";
        HttpHeaders headers = new HttpHeaders();
        headers.add("ApiKey", apiKey);
        headers.add("Cache-Control" , "no-cache");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<SvffClubResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SvffClubResponse.class
        );
        return response.getBody().getGames();
    }
    @Cacheable("svffTeams")
    public List<SvffTeamDto> fetchTeams()
    {
        String url = baseUrl+ "/club/details";
        HttpHeaders headers = new HttpHeaders();
        headers.add("ApiKey",apiKey);
        headers.add("Cache-Control" , "no-cache");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<SvffClubResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SvffClubResponse.class
        );
return response.getBody().getTeams();

    }
}
