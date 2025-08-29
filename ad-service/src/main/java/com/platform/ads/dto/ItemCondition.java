package com.platform.ads.dto;

public enum ItemCondition {
    ALL("Всички"),
    NEW("Нов"),
    USED("Употребяван"),
    FOR_PARTS("На части");

    private final String displayName;
    ItemCondition(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
