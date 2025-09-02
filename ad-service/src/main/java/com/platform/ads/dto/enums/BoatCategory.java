package com.platform.ads.dto.enums;

public enum BoatCategory {
    MOTOR_BOATS("Motor Boats/Yachts"),
    SAILBOATS("Sailboats/Yachts"),
    KAYAKS("Kayaks/Canoes");

    private final String displayName;

    BoatCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BoatCategory fromString(String category) {
        for (BoatCategory bc : BoatCategory.values()) {
            if (bc.name().equalsIgnoreCase(category)) {
                return bc;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + category);
    }
}
