package com.platform.ads.dto.enums;

public enum SortOrder {
    PRICE_LOW_TO_HIGH("Най-ниска цена"),
    PRICE_HIGH_TO_LOW("Най-висока цена"),
    NEWEST("Най-нови обяви"),
    OLDEST("Най-стари обяви"),
    MOST_VIEWED("Най-гледани");

    private final String displayName;
    SortOrder(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
