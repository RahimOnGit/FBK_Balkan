package com.example.fbk_balkan.enums;

public enum SponsorCategory {
    MAIN("Huvudsponsor"),
    GOLD("Guldsponsor"),
    SILVER("Silversponsor"),
    BRONZE("Bronsponsor"),
    PARENT("Föräldersponsor");

    private final String displayName;

    SponsorCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
