package com.merbsconnect.enums;

/**
 * Enum representing how a registrant heard about the event.
 */
public enum ReferralSource {
    SOCIAL_MEDIA("Social Media (Facebook, Instagram, Twitter, etc.)"),
    WHATSAPP("WhatsApp"),
    FRIEND_OR_COLLEAGUE("Friend or Colleague"),
    UNIVERSITY_NOTICE("University Notice Board/Announcement"),
    EMAIL("Email"),
    WEBSITE("Website/Blog"),
    FLYER_POSTER("Flyer/Poster"),
    RADIO("Radio"),
    CHURCH_FELLOWSHIP("Church/Fellowship"),
    PREVIOUS_EVENT("Attended Previous Event"),
    SPEAKER_RECOMMENDATION("Speaker Recommendation"),
    DEPARTMENT_FACULTY("Department/Faculty"),
    STUDENT_ORGANIZATION("Student Organization"),
    OTHER("Other");

    private final String displayName;

    ReferralSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
