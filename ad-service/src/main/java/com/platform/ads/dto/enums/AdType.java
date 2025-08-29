package com.platform.ads.dto.enums;

public enum AdType {
    FROM_PRIVATE("От частно лице"),
    FROM_COMPANY("От фирма");

    private final String displayName;

    AdType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
