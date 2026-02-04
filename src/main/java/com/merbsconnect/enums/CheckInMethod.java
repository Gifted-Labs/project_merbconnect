package com.merbsconnect.enums;

/**
 * Enum representing the method used for event check-in.
 */
public enum CheckInMethod {
    /**
     * Admin manually entered participant info or confirmed check-in.
     */
    MANUAL,

    /**
     * Check-in performed by scanning QR code.
     */
    QR_SCAN,

    /**
     * Bulk check-in operation for multiple participants.
     */
    BULK
}
