package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarineAccessoriesSpecificationDto {

    @NotNull(message = "Accessory type is required")
    private AccessoryType type;

    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    @NotNull(message = "Condition is required")
    private ItemCondition condition;

    public enum AccessoryType {
        ALL("Всички"),
        LIFE_JACKETS("Спасителни жилетки"),
        COSMETICS_CHEMICALS("Препарати и козметика"),
        HYDROFOIL("Хидрофойл"),
        FENDERS_BUOYS("Кранци и буйове"),
        OARS("Гребла"),
        COOLER_BAGS("Хладилни чанти"),
        INSTRUMENTS("Уреди"),
        COMPASSES("Компаси"),
        COVERS("Капаци за морска техника"),
        TARPS("Покривала"),
        KNIVES("Ножове"),
        ANCHORS("Котви"),
        ROPES("Въжета"),
        CONSOLES("Конзоли"),
        SEATS_CUSHIONS("Седалки и възглавници"),
        OTHER("Други");

        private final String displayName;
        AccessoryType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}