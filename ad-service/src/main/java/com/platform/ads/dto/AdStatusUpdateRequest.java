package com.platform.ads.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdStatusUpdateRequest {

    @NotNull(message = "Active status is required")
    private Boolean active;
}
