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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
                "24 hours");

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
                "1 hour");

        sendEmail(user.getEmail(), subject, content);
        log.info("Password reset email sent to: {}", user.getEmail());
    }

    @Override
    @Async
    public void sendRegistrationConfirmationEmail(String email, String name, String eventTitle,
            LocalDate eventDate, LocalTime eventTime,
            String eventLocation, String qrCodeBase64,
            String registrationToken) {
        String subject = "Registration Confirmed: " + eventTitle;

        String dateStr = eventDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String timeStr = eventTime != null ? eventTime.format(DateTimeFormatter.ofPattern("h:mm a")) : "TBA";

        String content = buildRegistrationConfirmationEmail(name, eventTitle, dateStr, timeStr,
                eventLocation, qrCodeBase64, registrationToken);

        sendEmail(email, subject, content);
        log.info("Registration confirmation email sent to: {} for event: {}", email, eventTitle);
    }

    @Override
    @Async
    public void sendEventReminderEmail(String email, String name, String eventTitle,
            LocalDate eventDate, LocalTime eventTime, String eventLocation) {
        String subject = "Reminder: " + eventTitle + " is Coming Up!";

        String dateStr = eventDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String timeStr = eventTime != null ? eventTime.format(DateTimeFormatter.ofPattern("h:mm a")) : "TBA";

        String content = buildEventReminderEmail(name, eventTitle, dateStr, timeStr, eventLocation);

        sendEmail(email, subject, content);
        log.info("Event reminder email sent to: {} for event: {}", email, eventTitle);
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

    /**
     * Builds the registration confirmation email HTML content.
     */
    private String buildRegistrationConfirmationEmail(String name, String eventTitle, String eventDate,
            String eventTime, String location,
            String qrCodeBase64, String registrationToken) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #c41e3a, #8b0000); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .qr-section { text-align: center; margin: 30px 0; padding: 20px; background: white; border-radius: 10px; }
                        .qr-code { max-width: 200px; margin: 15px auto; }
                        .event-details { background: white; padding: 20px; border-radius: 10px; margin: 20px 0; }
                        .event-details h3 { color: #c41e3a; margin-top: 0; }
                        .detail-row { padding: 8px 0; border-bottom: 1px solid #eee; }
                        .detail-label { color: #666; font-weight: 600; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                        .token { font-family: monospace; background: #e8e8e8; padding: 5px 10px; border-radius: 4px; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1 style="margin: 0;">🎉 Registration Confirmed!</h1>
                            <p style="margin: 10px 0 0;">You're all set for the event</p>
                        </div>
                        <div class="content">
                            <p>Hi <strong>%s</strong>,</p>
                            <p>Your registration for <strong>%s</strong> has been confirmed. We're excited to have you join us!</p>

                            <div class="qr-section">
                                <h3 style="margin-top: 0;">Your Check-in QR Code</h3>
                                <p>Present this QR code at the event entrance for quick check-in.</p>
                                <img class="qr-code" src="%s" alt="Check-in QR Code" />
                                <p class="token">Token: %s</p>
                            </div>

                            <div class="event-details">
                                <h3>📅 Event Details</h3>
                                <div class="detail-row">
                                    <span class="detail-label">Event:</span> %s
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Date:</span> %s
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Time:</span> %s
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Location:</span> %s
                                </div>
                            </div>

                            <p><strong>Important:</strong> Please save this email or take a screenshot of your QR code for check-in.</p>
                        </div>
                        <div class="footer">
                            <p>This email was sent by MerbsConnect. If you have any questions, please contact us.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(name, eventTitle, qrCodeBase64, registrationToken, eventTitle, eventDate, eventTime,
                        location != null ? location : "TBA");
    }

    /**
     * Builds the event reminder email HTML content.
     */
    private String buildEventReminderEmail(String name, String eventTitle, String eventDate,
            String eventTime, String location) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #c41e3a, #8b0000); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .event-details { background: white; padding: 20px; border-radius: 10px; margin: 20px 0; }
                        .event-details h3 { color: #c41e3a; margin-top: 0; }
                        .detail-row { padding: 8px 0; border-bottom: 1px solid #eee; }
                        .detail-label { color: #666; font-weight: 600; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1 style="margin: 0;">⏰ Event Reminder</h1>
                            <p style="margin: 10px 0 0;">Don't forget - the event is coming up!</p>
                        </div>
                        <div class="content">
                            <p>Hi <strong>%s</strong>,</p>
                            <p>This is a friendly reminder that <strong>%s</strong> is coming up soon. We look forward to seeing you there!</p>

                            <div class="event-details">
                                <h3>📅 Event Details</h3>
                                <div class="detail-row">
                                    <span class="detail-label">Event:</span> %s
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Date:</span> %s
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Time:</span> %s
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Location:</span> %s
                                </div>
                            </div>

                            <p><strong>Remember:</strong> Bring your QR code (from your registration confirmation email) for quick check-in!</p>
                        </div>
                        <div class="footer">
                            <p>This email was sent by MerbsConnect. If you have any questions, please contact us.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(name, eventTitle, eventTitle, eventDate, eventTime, location != null ? location : "TBA");
    }
}
