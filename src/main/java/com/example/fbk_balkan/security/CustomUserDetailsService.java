package com.example.fbk_balkan.security;

import com.example.fbk_balkan.entity.Coach;
import com.example.fbk_balkan.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CoachRepository coachRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Coach coach = coachRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Coach not found with username: " + username));

        return User.builder()
                .username(coach.getUsername())
                .password(coach.getPassword())
                .authorities(getAuthorities(coach))
                .disabled(!coach.isEnabled())
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Coach coach) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + coach.getRole()));
        return authorities;
    }
}