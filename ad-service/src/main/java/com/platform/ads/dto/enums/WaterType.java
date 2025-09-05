package com.platform.ads.dto.enums;

public enum WaterType {
    ALL("Всички"),
    FRESHWATER("Сладководен"),
    SALTWATER("Соленоводен");

    private final String displayName;
    WaterType(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
