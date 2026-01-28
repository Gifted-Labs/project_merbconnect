package com.merbsconnect.events.model;

/**
 * Enum representing the type of itinerary item in an event program.
 */
public enum ItineraryItemType {
    CEREMONY("Ceremony"),
    SESSION("Session/Talk"),
    WORKSHOP("Workshop"),
    PANEL("Panel Discussion"),
    BREAK("Break"),
    MEAL("Meal/Refreshments"),
    NETWORKING("Networking"),
    WORSHIP("Worship/Prayer"),
    REGISTRATION("Registration/Check-in"),
    ENTERTAINMENT("Entertainment"),
    AWARDS("Awards/Recognition"),
    CLOSING("Closing Ceremony"),
    OTHER("Other");

    private final String displayName;

    ItineraryItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
