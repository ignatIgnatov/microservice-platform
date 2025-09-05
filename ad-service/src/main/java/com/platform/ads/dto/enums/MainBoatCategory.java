package com.platform.ads.dto.enums;

public enum MainBoatCategory {
    BOATS_AND_YACHTS("Лодки и Яхти"),
    JET_SKIS("Джетове"),
    TRAILERS("Колесари"),
    MARINE_ELECTRONICS("Морска Електроника"),
    ENGINES("Двигатели"),
    FISHING("Риболов"),
    WATER_SPORTS("Водни спортове"),
    PARTS("Части"),
    MARINE_ACCESSORIES("Морски аксесоари"),
    SERVICES("Услуги"),
    RENTALS("Под Наем");

    private final String displayName;

    MainBoatCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}