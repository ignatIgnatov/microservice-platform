package com.platform.ads.dto;

import com.platform.ads.dto.enums.ItemCondition;
import jakarta.validation.constraints.NotBlank;
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
public class WaterSportsSpecificationDto {

    @NotNull(message = "Water sports type is required")
    private WaterSportsType type;

    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    @NotNull(message = "Condition is required")
    private ItemCondition condition;

    public enum WaterSportsType {
        ALL("Всички"),
        SURF("Сърф"),
        SUP("SUP"),
        DIVING("Гмуркане"),
        WATER_SKIING("Водни ски"),
        PARAGLIDING("Параглайдинг"),
        WAKEBOARD("Уейкборд"),
        WINDSURF("Уиндсърф"),
        JETPACK("Джетпак"),
        FLYBOARD("Флайборд"),
        SAILING("Ветроходство"),
        ROWING("Гребане"),
        WATER_SPORTS_CLOTHING("Облекло за водни спортове");

        private final String displayName;
        WaterSportsType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}