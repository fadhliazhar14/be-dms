package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.entity.PasswordResetToken;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.exception.BusinessValidationException;
import com.bank_dki.be_dms.repository.PasswordResetTokenRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    @Value("${app.reset-password-link}")
    private String resetPasswordLink;

    /**
     * Creates password reset token and sends email notification
     * @param email User email address
     * @throws RuntimeException if user not found or email sending fails
     */
    public void createPasswordResetToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        User user = userRepository.findByUserEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BusinessValidationException("User not found with email: " + email));

        // Invalidate existing tokens for this user (security best practice)
        invalidateExistingTokens(user);
        
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        tokenRepository.save(resetToken);

        // Send password reset email via EmailService
        String resetUrl = resetPasswordLink + token;
        try {
            emailService.sendPasswordResetEmail(user, token, resetUrl);
            log.info("Password reset token created and email sent for user: {}", user.getUserEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email, but token was created for user: {}", user.getUserEmail(), e);
            // Still throw exception as user expects to receive email
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    /**
     * Invalidate all existing password reset tokens for a user
     * This prevents token reuse and improves security
     * @param user User entity
     */
    private void invalidateExistingTokens(User user) {
        var existingTokens = tokenRepository.findByUser(user);
        if (!existingTokens.isEmpty()) {
            tokenRepository.deleteAll(existingTokens);
            log.info("Invalidated {} existing tokens for user: {}", existingTokens.size(), user.getUserEmail());
        }
    }

    /**
     * Reset user password using valid reset token
     * @param token Reset token
     * @param newPassword New password (will be hashed)
     * @throws RuntimeException if token invalid, expired, or password requirements not met
     */
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        PasswordResetToken resetToken = tokenRepository.findByToken(token.trim())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Clean up expired token
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Reset token has expired. Please request a new password reset.");
        }

        User user = resetToken.getUser();
        user.setUserHashPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clean up used token
        tokenRepository.delete(resetToken);
        
        // Also invalidate any other tokens for this user
        invalidateExistingTokens(user);
        
        log.info("Password successfully reset for user: {}", user.getUserEmail());
    }
    
    /**
     * Validate if a reset token is valid and not expired
     * @param token Reset token
     * @return true if token is valid
     */
    public boolean isValidResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        return tokenRepository.findByToken(token.trim())
                .map(resetToken -> resetToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}

