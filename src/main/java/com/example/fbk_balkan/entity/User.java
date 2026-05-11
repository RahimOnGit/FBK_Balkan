package com.example.fbk_balkan.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;//     NEW field

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    private boolean enabled = true;

    // ============= Brute-force protection =============
    @Builder.Default
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Builder.Default
    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();   // ← fortfarande coach i Team-tabellen

    // NEW: teams where user is assistant coach
    @Builder.Default
    @ManyToMany(mappedBy = "assistantCoaches")
    private List<Team> assistantTeams = new ArrayList<>();
    // hjälpfunktioner (samma som tidigare)
    public void addTeam(Team team) {
        teams.add(team);
        team.setCoach(this);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        team.setCoach(null);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
