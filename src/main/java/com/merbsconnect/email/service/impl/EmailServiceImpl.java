package com.merbsconnect.email.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.email.exception.EmailSendException;
import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.email.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailTemplateService templateService;
    private final ObjectMapper objectMapper;

    @Value("${app.resend.api-key}")
    private String resendApiKey;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String frontendUrl;

    @Override
    @Async
    public void sendVerificationEmail(User user, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        String subject = "Verify your email address";
        String fullName = user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "");
        String content = templateService.getVerificationEmailContent(fullName, verificationUrl, "24 hours");

        sendEmail(user.getEmail(), subject, content);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    @Override
    @Async
    public void sendPasswordResetEmail(User user, VerificationToken token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token.getToken();
        String subject = "Reset your password";
        String fullName = user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "");
        String content = templateService.getPasswordResetEmailContent(fullName, resetUrl, "24 hours");

        sendEmail(user.getEmail(), subject, content);
        log.info("Password reset email sent to: {}", user.getEmail());
    }

    @Override
    @Async
    public void sendRegistrationConfirmationEmail(String email, String name, String eventTitle,
            LocalDate eventDate, LocalTime eventTime, String eventLocation,
            String qrCodeBase64, String registrationToken) {

        log.info("[EMAIL START] ============================================");
        log.info("[EMAIL START] Sending registration confirmation to: {}", email);

        try {
            String subject = "Registration Confirmed - " + eventTitle;

            String dateStr = eventDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
            String timeStr = eventTime != null ? eventTime.format(DateTimeFormatter.ofPattern("h:mm a")) : "TBA";

            // Extract raw base64 for attachment
            String rawBase64 = qrCodeBase64;
            if (qrCodeBase64.startsWith("data:")) {
                rawBase64 = qrCodeBase64.substring(qrCodeBase64.indexOf(",") + 1);
            }

            log.info("[EMAIL] QR code prepared (length: {} chars)", rawBase64.length());

            // Build email content with CID reference for the QR code
            String content = buildRegistrationConfirmationEmailWithEmbeddedQR(
                    name, eventTitle, dateStr, timeStr, eventLocation, registrationToken, "cid:qrcode");

            if (content == null || content.isBlank()) {
                log.error("[EMAIL FAILED] Email content generation returned empty!");
                return;
            }

            // Send via HttpClient
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", fromEmail);
            payload.put("to", List.of(email));
            payload.put("subject", subject);
            payload.put("html", content);

            // Add CID attachment
            Map<String, String> attachment = new HashMap<>();
            attachment.put("content", rawBase64);
            attachment.put("filename", "qrcode.png");
            attachment.put("content_id", "qrcode");
            payload.put("attachments", List.of(attachment));

            log.info("[EMAIL] Sending request to Resend API...");
            sendRawEmailRequest(payload);

            log.info("[EMAIL SUCCESS] Registration confirmation email sent!");

        } catch (Exception e) {
            log.error("[EMAIL FAILED] Failed to send registration email to: {}", email, e);
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
        Map<String, Object> payload = new HashMap<>();
        payload.put("from", fromEmail);
        payload.put("to", List.of(to));
        payload.put("subject", subject);
        payload.put("html", htmlContent);

        try {
            sendRawEmailRequest(payload);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new EmailSendException("Failed to send email", e);
        }
    }

    /**
     * Sends a raw HTTP request to the Resend API.
     * This bypasses SDK compatibility issues and gives full control over JSON
     * payload.
     */
    private void sendRawEmailRequest(Map<String, Object> payload) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(payload);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Resend API Success: {}", response.body());
            } else {
                log.error("Resend API Error (Status {}): {}", response.statusCode(), response.body());
                throw new EmailSendException("Resend API error: " + response.statusCode());
            }
        }
    }

    private String buildRegistrationConfirmationEmailWithEmbeddedQR(String name, String eventTitle, String eventDate,
            String eventTime, String location, String registrationToken, String imageSrc) {
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
                .formatted(name, eventTitle, imageSrc, registrationToken, eventTitle, eventDate, eventTime,
                        location != null ? location : "TBA");
    }

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

        String adminEmail = "merblinasare10@gmail.com";
        String subject = "New T-Shirt Request – " + eventTitle;

        String content = buildTshirtOrderAdminEmail(registrantName, registrantEmail,
                registrantPhone, shirtSize, eventTitle);

        sendEmail(adminEmail, subject, content);
    }

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
