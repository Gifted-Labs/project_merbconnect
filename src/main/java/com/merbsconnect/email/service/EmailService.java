package com.merbsconnect.email.service;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EmailService {
        /**
         * Sends an account verification email with a verification token
         * 
         * @param user  the user to send verification email to
         * @param token the verification token
         */
        void sendVerificationEmail(User user, String token);

        /**
         * Sends a password reset email with a reset token
         * 
         * @param user  the user to send password reset email to
         * @param token the password reset token
         */
        void sendPasswordResetEmail(User user, VerificationToken token);

        /**
         * Sends an event registration confirmation email with QR code
         * 
         * @param email             recipient email address
         * @param name              participant name
         * @param eventTitle        title of the event
         * @param eventDate         date of the event
         * @param eventTime         time of the event
         * @param eventLocation     location of the event
         * @param qrCodeBase64      base64 encoded QR code image
         * @param registrationToken the registration token
         */
        void sendRegistrationConfirmationEmail(String email, String name, String eventTitle,
                        LocalDate eventDate, LocalTime eventTime,
                        String eventLocation, String qrCodeBase64,
                        String registrationToken);

        /**
         * Sends an event reminder email
         * 
         * @param email         recipient email address
         * @param name          participant name
         * @param eventTitle    title of the event
         * @param eventDate     date of the event
         * @param eventTime     time of the event
         * @param eventLocation location of the event
         */
        void sendEventReminderEmail(String email, String name, String eventTitle,
                        LocalDate eventDate, LocalTime eventTime, String eventLocation);

        /**
         * Sends a T-shirt order notification email to admin
         * 
         * @param registrantName  name of the person who ordered
         * @param registrantEmail email of the person who ordered
         * @param registrantPhone phone number of the person who ordered
         * @param shirtSize       the shirt size ordered
         * @param eventTitle      title of the event
         */
        void sendTshirtOrderAdminEmail(String registrantName, String registrantEmail,
                        String registrantPhone, String shirtSize, String eventTitle);
}
