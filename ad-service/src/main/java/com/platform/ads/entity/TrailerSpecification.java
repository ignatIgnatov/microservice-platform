package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("trailer_specifications")
public class TrailerSpecification {

    @Id
    private Long id;

    @Column("ad_id")
    private Long adId;

    @Column("trailer_type")
    private String trailerType;

    @Column("brand")
    private String brand;

    @Column("model")
    private String model;

    @Column("axle_count")
    private String axleCount;

    @Column("is_registered")
    private Boolean isRegistered;

    @Column("own_weight")
    private BigDecimal ownWeight;

    @Column("load_capacity")
    private BigDecimal loadCapacity;

    @Column("length")
    private BigDecimal length;

    @Column("width")
    private BigDecimal width;

    @Column("year")
    private Integer year;

    @Column("suspension_type")
    private String suspensionType;

    @Column("keel_rollers")
    private String keelRollers;

    @Column("in_warranty")
    private Boolean inWarranty;

    @Column("condition")
    private String condition;
}
