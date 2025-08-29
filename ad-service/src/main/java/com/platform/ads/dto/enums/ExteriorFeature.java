package com.platform.ads.dto.enums;

public enum ExteriorFeature {
    EXTENDED_PLATFORM("Удължена платформа"),
    HYDRAULIC_PLATFORM("Хидравлична платформа"),
    TENT("Тента"),
    T_TOP("T-Top"),
    SKI_PYLON("Ски пилон"),
    WIPERS("Чистачки"),
    ROD_HOLDERS("Стойки за въдици"),
    CUP_HOLDERS("Поставки за чаши"),
    LADDER("Стълба"),
    NAV_LIGHTS("Навигационни светлини"),
    DECK_SHOWER("Душ на палубата"),
    HARD_BOTTOM("Твърдо дъно"),
    SOFT_BOTTOM("Меко дъно");

    private final String displayName;
    ExteriorFeature(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
