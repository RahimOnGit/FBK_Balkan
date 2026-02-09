package com.example.fbk_balkan.enums;

public enum ReferralSource {
    PLAYER("Spelare i klubben"),
    COACH("Tränare i klubben"),
    FAMILY("Familj/Vän"),
    SOCIAL_MEDIA("Sociala medier"),
    WEBSITE("Klubbens hemsida"),
    EVENT("Lokalt evenemang"),
    SCHOOL("Skola/Skolidrott"),
    OTHER("Annat");

    private final String label;

    ReferralSource(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

