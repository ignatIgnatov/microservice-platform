package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("brands")
public class Brand {
    @Id
    private Long id;

    private String name;

    private String category; // MOTOR_BOATS, SAILBOATS, KAYAKS

    private Boolean active;

    @Column("display_order")
    private Integer displayOrder;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}