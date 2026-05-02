package com.example.fbk_balkan.service;

import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Brute-force protection: after MAX_ATTEMPTS failed logins the account is
 * locked for LOCK_DURATION_MINUTES. A successful login resets the counter.
 */
@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    public static final int LOCK_DURATION_MINUTES = 15;

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private final UserRepository userRepository;

    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns true if the account is currently locked (lockedUntil in the future).
     * Also clears an expired lock.
     */
    @Transactional
    public boolean isLocked(User user) {
        if (user.getLockedUntil() == null) return false;
        if (user.getLockedUntil().isAfter(LocalDateTime.now())) {
            return true;
        }
        // Expired lock: auto-clear
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        return false;
    }

    @Transactional
    public void loginSucceeded(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.getFailedLoginAttempts() != 0 || user.getLockedUntil() != null) {
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
        });
    }

    /**
     * Increments the failed-attempt counter. When it reaches MAX_ATTEMPTS the
     * account is locked for LOCK_DURATION_MINUTES. Returns true if the account
     * is locked as a result of this call (or was already locked).
     */
    @Transactional
    public boolean loginFailed(String email) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            // Don't reveal whether the email exists.
            return false;
        }
        User user = opt.get();

        if (isLocked(user)) {
            return true;
        }

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            LocalDateTime until = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
            user.setLockedUntil(until);
            log.warn("Account locked until {} for user {} after {} failed login attempts",
                    until, email, attempts);
            userRepository.save(user);
            return true;
        }

        userRepository.save(user);
        return false;
    }
}
