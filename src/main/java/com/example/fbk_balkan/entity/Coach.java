package com.example.fbk_balkan.entity;

import com.example.fbk_balkan.entity.Role;
import com.example.fbk_balkan.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coaches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean enabled = true;

    // ────────────────────────────────────────────────
    // Bidirektionell relation – en coach kan ha många lag
    // ────────────────────────────────────────────────
    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    // Hjälpmetod för att hålla relationen synkroniserad
    public void addTeam(Team team) {
        teams.add(team);
        team.setCoach(this);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        team.setCoach(null);
    }
}