package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ads")
public class Ad {
    @Id
    private Long id;
    private String title;
    private String description;
    private String quickDescription;
    private String category;
    private BigDecimal priceAmount;
    private String priceType;
    private Boolean includingVat;
    private String location;
    private String adType;
    private String userEmail;
    private String userId;
    private String userFirstName;
    private String userLastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;
    private Integer viewsCount;
    private Boolean featured;

    // New fields for approval workflow
    private String approvalStatus; // PENDING, APPROVED, REJECTED
    private String rejectionReason;
    private String approvedByUserId;
    private LocalDateTime approvedAt;
}