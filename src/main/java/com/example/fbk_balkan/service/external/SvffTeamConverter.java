package com.example.fbk_balkan.service.external;

import com.example.fbk_balkan.dto.svff.SvffTeamDto;
import com.example.fbk_balkan.entity.Team;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SvffTeamConverter {

    public Team toTeamEntity(SvffTeamDto svff) {
        Team team = new Team();
        team.setName(svff.getName());
        team.setGender(mapGender(svff.getGender()));
        team.setAgeGroup(buildAgeGroup(svff));
        team.setActive(true);
        team.setCreatedDate(LocalDateTime.now());
        team.setUpdatedDate(LocalDateTime.now());
        // coach, description, trainingLocation   .. admin fills later
        return team;
    }

    private Team.Gender mapGender(String gender) {
        if (gender == null) return Team.Gender.MALE;
        return gender.equalsIgnoreCase("Kvinna") ? Team.Gender.FEMALE : Team.Gender.MALE;
    }

    private String buildAgeGroup(SvffTeamDto svff) {
        String name     = svff.getName();
        String gender   = svff.getGender();
        String prefix   = gender != null && gender.equalsIgnoreCase("Kvinna") ? "Flickor" : "Pojkar";

        // U19, U17 etc → "U19"
        if (name.matches(".*U(\\d{2}).*")) {
            Matcher m = Pattern.compile("U(\\d{2})").matcher(name);
            if (m.find()) return "U" + m.group(1);
        }

        // "Flickor 12 år 2024" → born 2024-12 = 2012 → "Flickor 2012"
        if (name.contains("Flickor") && name.contains("år")) {
            Matcher ageMatcher = Pattern.compile("(\\d{1,2})\\s*år").matcher(name);
            Matcher seasonMatcher = Pattern.compile("(\\d{4})").matcher(name);
            if (ageMatcher.find() && seasonMatcher.find()) {
                int age = Integer.parseInt(ageMatcher.group(1));
                int season = Integer.parseInt(seasonMatcher.group(1));
                int birthYear = season - age;           // 2024 - 12 = 2012
                return "Flickor " + birthYear;          // "Flickor 2012"
            }
        }

        // Year range: "2014-2015" → "Pojkar 2014/2015"
        Matcher rangeMatcher = Pattern.compile("(\\d{4})-(\\d{4})").matcher(name);
        if (rangeMatcher.find()) {
            return prefix + " " + rangeMatcher.group(1) + "/" + rangeMatcher.group(2);
            // "Pojkar 2014/2015"
        }

        // Single birth year: "FBK Balkan 2018" → "Pojkar 2018"
        Matcher yearMatcher = Pattern.compile("(\\d{4})").matcher(name);
        if (yearMatcher.find()) {
            int year = Integer.parseInt(yearMatcher.group(1));
            if (year >= 2005 && year <= 2020) {         // birth years only
                return prefix + " " + year;             // "Pojkar 2018"
            }
        }

        // Fallback
        return name.replace("FBK Balkan", "").replaceAll("[^a-zA-ZåäöÅÄÖ0-9/ ]", "").trim();
    }
}