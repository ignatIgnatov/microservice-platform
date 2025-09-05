package com.platform.ads.dto.enums;

public enum BoatPurpose {
    ALL("Всички"),
    FISHING("Рибарска"),
    BEACH("Плажна"),
    WATER_SPORTS("Водни спортове"),
    WORK("Работна");

    private final String displayName;
    BoatPurpose(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
