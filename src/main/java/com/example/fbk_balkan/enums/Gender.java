package com.example.fbk_balkan.enums;

public enum Gender {
    MALE("Kille"),
    FEMALE("Tjej"),
    ANNAT("Annat"),
    VillEjAnge("Vill ej ange");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
