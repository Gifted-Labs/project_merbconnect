package com.merbsconnect.enums;

/**
 * Enum representing the academic level of a university student.
 */
public enum AcademicLevel {
    LEVEL_100("100 Level - Freshman"),
    LEVEL_200("200 Level - Sophomore"),
    LEVEL_300("300 Level - Junior"),
    LEVEL_400("400 Level - Senior"),
    LEVEL_500("500 Level - Fifth Year"),
    LEVEL_600("600 Level - Sixth Year"),
    GRADUATE("Graduate Student"),
    POSTGRADUATE("Postgraduate Student"),
    PHD("PhD Candidate"),
    ALUMNI("Alumni"),
    OTHER("Other");

    private final String displayName;

    AcademicLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
