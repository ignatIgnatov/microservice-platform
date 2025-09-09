package com.platform.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for business registration number validation")
public class ValidationResponse {

    @Schema(description = "Indicates if the business registration number format is valid", example = "true")
    private boolean valid;

    @Schema(description = "Indicates if the business registration number is available for use", example = "true")
    private boolean available;

    @Schema(description = "Validation message providing details about the result", example = "Business registration number is valid and available")
    private String message;

    public static ValidationResponse valid(String message) {
        return new ValidationResponse(true, true, message);
    }

    public static ValidationResponse invalid(String message) {
        return new ValidationResponse(false, false, message);
    }

    public static ValidationResponse unavailable(String message) {
        return new ValidationResponse(true, false, message);
    }
}