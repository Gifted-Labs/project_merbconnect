package com.merbsconnect.enums;

/**
 * Enum representing the academic level of a university student.
 */
public enum AcademicLevel {
    LEVEL_100("Level 100"),
    LEVEL_200("Level 200"),
    LEVEL_300("Level 300"),
    LEVEL_400("Level 400"),
    LEVEL_500("Level 500"),
    LEVEL_600("Level 600"),
    GRADUATE("Graduate Student");

    private final String displayName;

    AcademicLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
