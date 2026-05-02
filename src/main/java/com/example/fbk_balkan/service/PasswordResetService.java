package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.PasswordResetToken;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.PasswordResetTokenRepository;
import com.example.fbk_balkan.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    public static final int TOKEN_VALIDITY_MINUTES = 30;

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationService emailService;
    private final LoginAttemptService loginAttemptService;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailNotificationService emailService,
                                LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Generates a one-time UUID reset token for the user with the given email
     * and emails the reset link. If no user exists for the email, this method
     * silently does nothing (to avoid email enumeration attacks).
     */
    @Transactional
    public void createAndSendResetToken(String email, String baseUrl) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            log.info("Password reset requested for unknown email: {}", email);
            return;
        }
        User user = opt.get();

        // Invalidate any previous tokens for this user
        tokenRepository.deleteAllForUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES))
                .used(false)
                .build();
        tokenRepository.save(prt);

        String resetLink = baseUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user, resetLink, TOKEN_VALIDITY_MINUTES);

        log.info("Password reset token issued for user {} (expires {})",
                user.getEmail(), prt.getExpiresAt());
    }

    /**
     * Looks up a token and verifies it is not used/expired.
     * Returns the token if valid, otherwise empty.
     */
    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> findValidToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return tokenRepository.findByToken(token).filter(PasswordResetToken::isValid);
    }

    /**
     * Resets the user's password using a valid token. Marks the token as used,
     * unlocks the account, and resets the failed-attempt counter.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> opt = findValidToken(token);
        if (opt.isEmpty()) return false;

        PasswordResetToken prt = opt.get();
        User user = prt.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        // Unlock the account on successful reset
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);

        // Defensive: also clear any cached state
        loginAttemptService.loginSucceeded(user.getEmail());

        log.info("Password reset successful for user {}", user.getEmail());
        return true;
    }
}
