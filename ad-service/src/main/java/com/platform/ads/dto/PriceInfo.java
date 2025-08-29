package com.platform.ads.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfo {
    private BigDecimal amount;

    @NotNull(message = "Price type is required")
    private PriceType type;

    private Boolean includingVat;

    public enum PriceType {
        FIXED_PRICE("Цена"),
        FREE("Подарявам"),
        NEGOTIABLE("По договаряне"),
        BARTER("Бартер");

        private final String displayName;

        PriceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

