package com.merbsconnect.enums;

/**
 * Enum representing available shirt colors for T-shirt requests.
 */
public enum ShirtColor {
    BLACK("Black"),
    WHITE("White");

    private final String displayName;

    ShirtColor(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
