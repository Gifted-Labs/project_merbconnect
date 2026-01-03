package com.merbsconnect.email.service;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;

public interface EmailService {
    /**
     * Sends an account verification email with a verification token
     * @param user the user to send verification email to
     * @param token the verification token
     */
    void sendVerificationEmail(User user, String token);
    
    /**
     * Sends a password reset email with a reset token
     * @param user the user to send password reset email to
     * @param token the password reset token
     */
    void sendPasswordResetEmail(User user, VerificationToken token);
}