package com.platform.ads.model;

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

    @Column("price")
    private BigDecimal price;

    @Column("category")
    private String category;

    @Column("location")
    private String location;

    @Column("user_email")
    private String userEmail;

    @Column("user_id")
    private String userId; // Keycloak user ID

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
}