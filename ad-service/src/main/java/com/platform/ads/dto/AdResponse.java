package com.platform.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdResponse {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private String category;

    private String location;

    private String userEmail;

    private String userId;

    private String userFirstName;

    private String userLastName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean active;

    // Computed fields
    private String userFullName;

    public String getUserFullName() {
        if (userFirstName != null && userLastName != null) {
            return userFirstName + " " + userLastName;
        } else if (userFirstName != null) {
            return userFirstName;
        } else if (userLastName != null) {
            return userLastName;
        }
        return userEmail; // Fallback to email
    }
}