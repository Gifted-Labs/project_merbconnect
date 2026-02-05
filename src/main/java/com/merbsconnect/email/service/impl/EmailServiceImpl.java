package com.merbsconnect.email.service.impl;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.email.exception.EmailSendException;
import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.email.service.EmailTemplateService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

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
        try {
            log.info("[EMAIL] Starting verification email to: {}", user.getEmail());

            // Validate inputs
            if (user == null || user.getEmail() == null) {
                log.error("[EMAIL FAILED] Cannot send verification email - user or email is null");
                return;
            }
            if (token == null || token.isBlank()) {
                log.error("[EMAIL FAILED] Cannot send verification email - token is null or blank");
                return;
            }

            // Validate configuration
            if (resendApiKey == null || resendApiKey.isBlank()) {
                log.error("[EMAIL FAILED] Resend API key is not configured!");
                return;
            }
            if (fromEmail == null || fromEmail.isBlank()) {
                log.error("[EMAIL FAILED] From email is not configured!");
                return;
            }

            String verificationLink = baseUrl + "/api/v1/auth/verify-email?token=" + token;
            String subject = "Verify Your Email Address";

            String content = templateService.getVerificationEmailContent(
                    user.getFirstName(),
                    verificationLink,
                    "24 hours");

            sendEmail(user.getEmail(), subject, content);
            log.info("[EMAIL SUCCESS] Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("[EMAIL FAILED] Failed to send verification email to {}: {}",
                    user != null ? user.getEmail() : "null", e.getMessage());
            log.error("[EMAIL FAILED] Full stack trace:", e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(User user, VerificationToken token) {
        try {
            log.info("[EMAIL] Starting password reset email to: {}", user.getEmail());

            // Validate inputs
            if (user == null || user.getEmail() == null) {
                log.error("[EMAIL FAILED] Cannot send password reset email - user or email is null");
                return;
            }
            if (token == null || token.getToken() == null) {
                log.error("[EMAIL FAILED] Cannot send password reset email - token is null");
                return;
            }

            String resetLink = baseUrl + "/api/v1/auth/reset-password?token=" + token.getToken();
            String subject = "Password Reset Request";

            String content = templateService.getPasswordResetEmailContent(
                    user.getFirstName(),
                    resetLink,
                    "1 hour");

            sendEmail(user.getEmail(), subject, content);
            log.info("[EMAIL SUCCESS] Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("[EMAIL FAILED] Failed to send password reset email to {}: {}",
                    user != null ? user.getEmail() : "null", e.getMessage());
            log.error("[EMAIL FAILED] Full stack trace:", e);
        }
    }

    @Override
    @Async
    public void sendRegistrationConfirmationEmail(String email, String name, String eventTitle,
            LocalDate eventDate, LocalTime eventTime,
            String eventLocation, String qrCodeBase64,
            String registrationToken) {
        try {
            log.info("[EMAIL] ============================================");
            log.info("[EMAIL] Starting registration confirmation email");
            log.info("[EMAIL] To: {}, Event: {}", email, eventTitle);

            // ===== VALIDATE INPUTS =====
            if (email == null || email.isBlank()) {
                log.error("[EMAIL FAILED] Cannot send registration email - email is null or blank");
                return;
            }
            if (name == null || name.isBlank()) {
                log.error("[EMAIL FAILED] Cannot send registration email - name is null or blank");
                return;
            }
            if (eventTitle == null || eventTitle.isBlank()) {
                log.error("[EMAIL FAILED] Cannot send registration email - eventTitle is null or blank");
                return;
            }
            if (eventDate == null) {
                log.error("[EMAIL FAILED] Cannot send registration email - eventDate is null");
                return;
            }
            if (qrCodeBase64 == null || qrCodeBase64.isBlank()) {
                log.error("[EMAIL FAILED] Cannot send registration email - QR code is null or blank!");
                log.error("[EMAIL FAILED] This likely means QR code generation failed earlier.");
                return;
            }
            if (registrationToken == null || registrationToken.isBlank()) {
                log.error("[EMAIL FAILED] Cannot send registration email - registrationToken is null");
                return;
            }

            // ===== VALIDATE CONFIGURATION =====
            if (resendApiKey == null || resendApiKey.isBlank()) {
                log.error("[EMAIL FAILED] CRITICAL: Resend API key is NOT configured!");
                log.error("[EMAIL FAILED] Check app.resend.api-key in application.yaml");
                return;
            }
            if (fromEmail == null || fromEmail.isBlank()) {
                log.error("[EMAIL FAILED] CRITICAL: From email is NOT configured!");
                log.error("[EMAIL FAILED] Check app.email.from in application.yaml");
                return;
            }

            log.info("[EMAIL] Configuration OK - API Key: {}..., From: {}",
                    resendApiKey.substring(0, Math.min(8, resendApiKey.length())), fromEmail);

            // ===== BUILD EMAIL CONTENT =====
            String subject = "Registration Confirmed: " + eventTitle;
            String dateStr = eventDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
            String timeStr = eventTime != null ? eventTime.format(DateTimeFormatter.ofPattern("h:mm a")) : "TBA";

            log.info("[EMAIL] Building email content with embedded QR code...");

            // Ensure QR code has proper data URI prefix for embedding in HTML
            String qrDataUri = qrCodeBase64;
            if (!qrCodeBase64.startsWith("data:")) {
                qrDataUri = "data:image/png;base64," + qrCodeBase64;
            }
            log.info("[EMAIL] QR code prepared as data URI (length: {} chars)", qrDataUri.length());

            // Build email content with embedded QR code (data URI instead of CID
            // attachment)
            String content = buildRegistrationConfirmationEmailWithEmbeddedQR(
                    name, eventTitle, dateStr, timeStr, eventLocation, registrationToken, qrDataUri);

            if (content == null || content.isBlank()) {
                log.error("[EMAIL FAILED] Email content generation returned empty!");
                return;
            }
            log.info("[EMAIL] Email content built successfully (length: {} chars)", content.length());

            // ===== SEND EMAIL (without attachments - QR is embedded as data URI) =====
            log.info("[EMAIL] Sending email via Resend API (no attachments)...");
            sendEmail(email, subject, content);

            log.info("[EMAIL SUCCESS] ============================================");
            log.info("[EMAIL SUCCESS] Registration confirmation email sent!");
            log.info("[EMAIL SUCCESS] To: {}, Event: {}", email, eventTitle);

        } catch (Exception e) {
            log.error("[EMAIL FAILED] ============================================");
            log.error("[EMAIL FAILED] Failed to send registration email to: {}", email);
            log.error("[EMAIL FAILED] Event: {}", eventTitle);
            log.error("[EMAIL FAILED] Error message: {}", e.getMessage());
            log.error("[EMAIL FAILED] Full stack trace:", e);
            // DO NOT re-throw - this would be lost in async thread anyway
        }
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
     * Sends an email with an inline image attachment using Content-ID (CID).
     * This ensures the image renders in email clients that block base64 data URIs.
     *
     * @param to          recipient email address
     * @param subject     email subject
     * @param htmlContent HTML content with cid: reference (e.g., src="cid:qrcode")
     * @param imageBase64 base64 encoded image data (without data URI prefix)
     * @param contentId   Content-ID for the image (e.g., "qrcode")
     * @param filename    filename for the attachment (e.g., "qrcode.png")
     */
    private void sendEmailWithInlineImage(String to, String subject, String htmlContent,
            String imageBase64, String contentId, String filename) {
        try {
            Resend resend = new Resend(resendApiKey);

            // Create inline attachment with Content-ID
            Attachment qrAttachment = Attachment.builder()
                    .fileName(filename)
                    .content(imageBase64)
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .attachments(List.of(qrAttachment))
                    .build();

            SendEmailResponse response = resend.emails().send(request);

            if (response.getId() != null) {
                log.info("Email with inline image sent via Resend. ID: {}, To: {}", response.getId(), to);
            } else {
                log.error("Failed to send email with inline image via Resend to: {}", to);
                throw new EmailSendException("Failed to send email with inline image via Resend");
            }

        } catch (ResendException e) {
            log.error("Resend API error when sending email with image to {}: {}", to, e.getMessage());
            throw new EmailSendException("Failed to send email with inline image via Resend", e);
        } catch (Exception e) {
            log.error("Unexpected error when sending email with image to {}: {}", to, e.getMessage());
            throw new EmailSendException("Failed to send email with inline image", e);
        }
    }

    /**
     * Builds the registration confirmation email HTML content with embedded QR
     * code.
     * Uses data URI for QR code instead of CID attachment for better compatibility.
     */
    private String buildRegistrationConfirmationEmailWithEmbeddedQR(String name, String eventTitle, String eventDate,
            String eventTime, String location, String registrationToken, String qrDataUri) {
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
                                <img class="qr-code" src="%s" alt="Check-in QR Code" style="max-width: 200px; margin: 15px auto; display: block;" />
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
                .formatted(name, eventTitle, qrDataUri, registrationToken, eventTitle, eventDate, eventTime,
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

    @Override
    @Async
    public void sendTshirtOrderAdminEmail(String registrantName, String registrantEmail,
            String registrantPhone, String shirtSize, String eventTitle) {

        // Admin email for T-shirt orders
        String adminEmail = "juliusadjeteysowah@gmail.com";
        String subject = "New T-Shirt Request – " + eventTitle;

        String content = buildTshirtOrderAdminEmail(registrantName, registrantEmail,
                registrantPhone, shirtSize, eventTitle);

        sendEmail(adminEmail, subject, content);
        log.info("T-shirt order admin email sent for registrant: {} - Size: {}", registrantName, shirtSize);
    }

    /**
     * Builds the T-shirt order admin notification email HTML content.
     */
    private String buildTshirtOrderAdminEmail(String registrantName, String registrantEmail,
            String registrantPhone, String shirtSize, String eventTitle) {
        String timestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"));

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #1a1a2e, #16213e); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .order-details { background: white; padding: 20px; border-radius: 10px; margin: 20px 0; border-left: 4px solid #c41e3a; }
                        .order-details h3 { color: #c41e3a; margin-top: 0; }
                        .detail-row { padding: 10px 0; border-bottom: 1px solid #eee; display: flex; }
                        .detail-label { color: #666; font-weight: 600; width: 120px; }
                        .detail-value { color: #333; font-weight: 500; }
                        .size-badge { display: inline-block; background: #c41e3a; color: white; padding: 8px 20px; border-radius: 20px; font-weight: bold; font-size: 18px; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                        .action-note { background: #fff3cd; border: 1px solid #ffc107; padding: 15px; border-radius: 8px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1 style="margin: 0;">👕 New T-Shirt Order</h1>
                            <p style="margin: 10px 0 0;">A new T-shirt has been requested</p>
                        </div>
                        <div class="content">
                            <p>A participant has requested a T-shirt during event registration.</p>

                            <div class="order-details">
                                <h3>📋 Order Details</h3>
                                <div class="detail-row">
                                    <span class="detail-label">Name:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Email:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Phone:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Event:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Shirt Size:</span>
                                    <span class="size-badge">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Requested:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                            </div>

                            <div class="action-note">
                                <strong>⚡ Action Required:</strong> Please contact the registrant to arrange payment and delivery details.
                            </div>
                        </div>
                        <div class="footer">
                            <p>This notification was sent by MerbsConnect T-Shirt Order System.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(registrantName, registrantEmail,
                        registrantPhone != null ? registrantPhone : "Not provided",
                        eventTitle, shirtSize, timestamp);
    }
}
