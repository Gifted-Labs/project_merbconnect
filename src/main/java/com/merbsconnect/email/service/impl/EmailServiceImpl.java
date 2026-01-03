package com.merbsconnect.email.service.impl;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.email.exception.EmailSendException;
import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.email.service.EmailTemplateService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailTemplateService templateService;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${app.resend.api-key}")
    private String resendApiKey;

    @Override
    @Async
    public void sendVerificationEmail(User user, String token) {
        String verificationLink = baseUrl + "/api/v1/auth/verify-email?token=" + token;
        String subject = "Verify Your Email Address";
        
        String content = templateService.getVerificationEmailContent(
                user.getFirstName(), 
                verificationLink, 
                "24 hours"
        );
        
        sendEmail(user.getEmail(), subject, content);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    @Override
    @Async
    public void sendPasswordResetEmail(User user, VerificationToken token) {
        String resetLink = baseUrl + "/api/v1/auth/reset-password?token=" + token.getToken();
        String subject = "Password Reset Request";
        
        String content = templateService.getPasswordResetEmailContent(
                user.getFirstName(),
                resetLink,
                "1 hour"
        );
        
        sendEmail(user.getEmail(), subject, content);
        log.info("Password reset email sent to: {}", user.getEmail());
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            Resend resend = new Resend(resendApiKey);
            
            SendEmailRequest request = SendEmailRequest.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();
            
            SendEmailResponse response = resend.emails().send(request);
            
            if (response.getId() != null) {
                log.info("Email sent successfully via Resend. ID: {}, To: {}", response.getId(), to);
            } else {
                log.error("Failed to send email via Resend to: {}", to);
                throw new EmailSendException("Failed to send email via Resend");
            }
            
        } catch (ResendException e) {
            log.error("Resend API error when sending email to {}: {}", to, e.getMessage());
            throw new EmailSendException("Failed to send email via Resend", e);
        } catch (Exception e) {
            log.error("Unexpected error when sending email to {}: {}", to, e.getMessage());
            throw new EmailSendException("Failed to send email", e);
        }
    }
}