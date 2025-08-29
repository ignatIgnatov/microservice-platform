package com.platform.ads.dto;

public enum TrailerFeature {
    WINCH_STAND_WITH_ROLLER("Стойка за лебедка с ролка"),
    STRAP_WINCH("Лебедка с колан"),
    FOG_LIGHT("Светлина за мъгла"),
    OWN_BRAKE_SYSTEM("Собствена спирачна система"),
    WATERPROOF_HUBS("Водоустойчиви главини"),
    WATERPROOF_LED_LIGHTS("Водоустойчиви предни LED светлини"),
    FOLDABLE_REAR_LIGHTS("Сгъваеми задни светлини"),
    FASTENING_RINGS("Крепежни халки"),
    ADJUSTABLE_HEIGHT_WINCH_STAND("Стойка за лебедка с регулируема височина"),
    EASY_ADJUST_SIDE_ROLLERS("Лесни за регулиране странични ролки"),
    TILTING_REAR_FRAME("Задна накланяща се рамка снабдена с опорни колела"),
    METAL_STEP_ON_FENDERS("Метално стъпало върху калниците"),
    SIDE_STEPS("Странични степенки"),
    SPARE_TIRE_WITH_RIM("Резервна гума с джанта");

    private final String displayName;
    TrailerFeature(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
