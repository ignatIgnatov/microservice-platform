package com.platform.ads.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("boat_equipment")
public class BoatEquipment {

    @Id
    private Long id;

    @Column("boat_spec_id")
    private Long boatSpecId;

    @Column("equipment")
    private String equipment;
}
