package com.example.fbk_balkan.security;

import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.service.LoginAttemptService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UserRepository userRepository,
                                    LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));

        boolean accountNonLocked = !loginAttemptService.isLocked(user);

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                accountNonLocked,
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }
}
