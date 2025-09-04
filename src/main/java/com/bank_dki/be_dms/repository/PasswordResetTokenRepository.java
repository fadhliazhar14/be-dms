package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.PasswordResetToken;
import com.bank_dki.be_dms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Find password reset token by token string
     * @param token Reset token
     * @return Optional PasswordResetToken
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Find all password reset tokens for a specific user
     * @param user User entity
     * @return List of PasswordResetToken
     */
    List<PasswordResetToken> findByUser(User user);
    
    /**
     * Delete all expired tokens (cleanup utility)
     * @param currentTime Current time for comparison
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :currentTime")
    void deleteExpiredTokens(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Count valid tokens for a user (for rate limiting)
     * @param user User entity
     * @param currentTime Current time
     * @return Count of valid tokens
     */
    @Query("SELECT COUNT(p) FROM PasswordResetToken p WHERE p.user = :user AND p.expiryDate > :currentTime")
    long countValidTokensByUser(@Param("user") User user, @Param("currentTime") LocalDateTime currentTime);
}
