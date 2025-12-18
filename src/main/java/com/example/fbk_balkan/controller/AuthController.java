package com.example.fbk_balkan.controller;

import com.example.fbk_balkan.dto.*;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.UserRepository;
import com.example.fbk_balkan.security.JwtUtils;
import com.example.fbk_balkan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPasswordHash(encoder.encode(registerRequest.getPassword()));

        try {
            user.setRole(User.Role.valueOf(registerRequest.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Invalid role. Must be PARENT or COACH");
        }

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(loginRequest.getEmail(), 
                    userRepository.findByEmail(loginRequest.getEmail())
                            .map(u -> u.getRole().name())
                            .orElse("USER"));

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get dashboard data based on user role
            DashboardData dashboardData = userService.getDashboardData(user.getUserId());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    user.getUserId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole().name(),
                    dashboardData));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Invalid email or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Since we're using JWT (stateless), the token is invalidated client-side
        // In a production system with token blacklisting, you would add the token to a blacklist here
        return ResponseEntity.ok("Logout successful");
    }
}

