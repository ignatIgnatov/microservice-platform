package com.platform.ads.dto.enums;

public enum BoatCategory {
    BOATS_AND_YACHTS("Лодки и Яхти"),
    JET_SKIS("Джетове"),
    TRAILERS("Колесари"),
    MARINE_ELECTRONICS("Морска Електроника"),
    ENGINES("Двигатели"),
    FISHING("Риболов"),
    PARTS("Части"),
    SERVICES("Услуги");

    private final String displayName;

    BoatCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
