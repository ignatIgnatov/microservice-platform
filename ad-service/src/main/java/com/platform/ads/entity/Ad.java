package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("quick_description")
    private String quickDescription;

    @Column("category")
    private String category;

    @Column("price_amount")
    private BigDecimal priceAmount;

    @Column("price_type")
    private String priceType;

    @Column("including_vat")
    private Boolean includingVat;

    @Column("location")
    private String location;

    @Column("ad_type")
    private String adType;

    @Column("user_email")
    private String userEmail;

    @Column("user_id")
    private String userId;

    @Column("user_first_name")
    private String userFirstName;

    @Column("user_last_name")
    private String userLastName;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("active")
    private Boolean active;

    @Column("views_count")
    private Integer viewsCount;

    @Column("featured")
    private Boolean featured;
}