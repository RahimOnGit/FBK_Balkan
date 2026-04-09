package com.example.fbk_balkan.enums;

public enum SponsorCategory {
//    MAIN("Huvudsponsor"),
//    GOLD("Guldsponsor"),
//    SILVER("Silversponsor"),
//    BRONZE("Bronsponsor"),
//    PARENT("Föräldersponsor");

MAIN("Huvudpartner"),
    GOLD("Företagspartners"),
    SILVER("Samarbetspartners"),
    SPRING_BALL("Vårens Bollpartner"),
    AUTUMN_BALL("Höstens Bollpartner"),
    BRONZE("Stödpartners"),
    PARENT("Barnens Partner");

    private final String displayName;

    SponsorCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
