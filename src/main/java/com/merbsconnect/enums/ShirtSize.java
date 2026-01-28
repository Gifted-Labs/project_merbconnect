package com.merbsconnect.enums;

/**
 * Enum representing available shirt sizes for event registration.
 */
public enum ShirtSize {
    XS("Extra Small"),
    S("Small"),
    M("Medium"),
    L("Large"),
    XL("Extra Large"),
    XXL("2X Large"),
    XXXL("3X Large");

    private final String displayName;

    ShirtSize(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
