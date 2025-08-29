package com.platform.ads.dto.enums;

public enum InteriorFeature {
    VINYL("Винил"),
    NATURAL_LEATHER("Естествена Кожа"),
    SYNTHETIC_LEATHER("Изкуствена кожа"),
    VELOUR("Велур"),
    TEAK_DECKING("Тиков декинг"),
    SYNTHETIC_TEAK("Изкуствен тиков декинг"),
    COCKPIT("Котлон"),
    SINK("Мивка"),
    STOVE("Готварска печка"),
    MICROWAVE("Микровълнова"),
    FRIDGE("Хладилник"),
    DISHWASHER("Съдомиялна"),
    EXTRACTOR("Абсорбатор"),
    SMOKE_DETECTOR("Детектор за дим"),
    COUNTER("Плот"),
    TABLE("Маса"),
    TOILET("Тоалетна"),
    SHOWER_CABIN("Душ кабина"),
    SHOWER("Душ"),
    AC("Климатик"),
    HEATER("Печка"),
    PILLOWS("Възглавници"),
    BERTHS("Лежанки"),
    SINGLE_BED("Единично легло"),
    DOUBLE_BED("Двойно легло"),
    BEDROOM("Спалня"),
    POWER_OUTLETS("Контакти"),
    LIVE_WELL("Живарник"),
    LIGHTS("Светлини"),
    CURTAINS("Пердета");

    private final String displayName;
    InteriorFeature(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
