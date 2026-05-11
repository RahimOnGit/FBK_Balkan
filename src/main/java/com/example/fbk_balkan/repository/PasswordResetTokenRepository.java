package com.example.fbk_balkan.repository;

import com.example.fbk_balkan.entity.PasswordResetToken;
import com.example.fbk_balkan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user")
    void deleteAllForUser(User user);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :cutoff")
    void deleteExpired(LocalDateTime cutoff);
}
