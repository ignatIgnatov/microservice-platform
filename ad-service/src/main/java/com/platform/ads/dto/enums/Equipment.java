package com.platform.ads.dto.enums;

public enum Equipment {
    SONAR("Сонар"),
    DEPTH_FINDER("Сонда"),
    RADAR("Радар"),
    AIS("AIS"),
    VHF("УКВ"),
    BOW_THRUSTER("Bow Thruster"),
    SPEAKERS("Тонколони"),
    MARINE_RADIO("Морско радио"),
    TROLLING_MOTOR("Тролинг мотор"),
    AUTOPILOT("Автопилот"),
    ELECTRIC_WINCH("Ел.лебедка"),
    SHORE_POWER("Брегово захранване"),
    STARTING_BATTERY("Стартов акумулатор"),
    TROLLING_BATTERY("Тягов акумулатор"),
    ANCHOR("Котва"),
    ENGINE("Двигател"),
    AUXILIARY_ENGINE("Помощен двигател"),
    ACTIVE_TRIM("Active Trim"),
    FENDERS("Кранци"),
    TRAILER("Колесар"),
    INSTRUMENTS("Уреди"),
    STERN_DRIVE("Щамбайн"),
    HYDROFOIL("Хидрофойл"),
    ELECTRIC_STABILIZER("Електрически стабилизатор"),
    GENERATOR("Генератор"),
    BILGE_PUMP("Билдж помпа/осушителна");

    private final String displayName;
    Equipment(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
