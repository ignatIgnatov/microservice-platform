-- V2__Create_boat_specifications.sql
-- Boat specifications and related feature tables

-- Boat specifications table
CREATE TABLE boat_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    boat_type VARCHAR(50) NOT NULL CHECK (boat_type IN ('ALL', 'MOTOR_BOAT', 'SAILING_BOAT', 'KAYAK_CANOE')),
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    engine_type VARCHAR(20) NOT NULL CHECK (engine_type IN ('ALL', 'OUTBOARD', 'INBOARD', 'NONE')),
    engine_included BOOLEAN NOT NULL,
    engine_brand_model VARCHAR(200),
    horsepower INTEGER NOT NULL CHECK (horsepower > 0 AND horsepower <= 10000),
    length DECIMAL(6,2) NOT NULL CHECK (length > 0 AND length <= 500),
    width DECIMAL(6,2) NOT NULL CHECK (width > 0 AND width <= 100),
    draft DECIMAL(5,2) CHECK (draft >= 0 AND draft <= 50),
    max_people INTEGER NOT NULL CHECK (max_people > 0 AND max_people <= 1000),
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    in_warranty BOOLEAN NOT NULL,
    weight DECIMAL(8,2) NOT NULL CHECK (weight > 0),
    fuel_capacity DECIMAL(7,2) NOT NULL CHECK (fuel_capacity >= 0),
    has_water_tank BOOLEAN NOT NULL,
    number_of_engines INTEGER NOT NULL CHECK (number_of_engines >= 0 AND number_of_engines <= 10),
    has_auxiliary_engine BOOLEAN NOT NULL,
    console_type VARCHAR(30) CHECK (console_type IN ('ALL', 'NONE', 'CENTRAL', 'SIDE', 'CABIN', 'FLYBRIDGE')),
    fuel_type VARCHAR(20) CHECK (fuel_type IN ('ALL', 'PETROL', 'DIESEL', 'LPG', 'HYDROGEN')),
    material VARCHAR(30) CHECK (material IN ('ALL', 'FIBERGLASS', 'WOOD', 'ALUMINUM', 'PVC', 'HYPALON', 'RUBBER')),
    is_registered BOOLEAN NOT NULL,
    has_commercial_fishing_license BOOLEAN,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    -- Unique constraint to prevent multiple specs for same ad
    CONSTRAINT unique_boat_spec_per_ad UNIQUE(ad_id)
);

-- Feature tables for many-to-many relationships
CREATE TABLE boat_interior_features (
    id BIGSERIAL PRIMARY KEY,
    boat_spec_id BIGINT NOT NULL REFERENCES boat_specifications(id) ON DELETE CASCADE,
    feature VARCHAR(50) NOT NULL,

    CONSTRAINT unique_interior_feature_per_boat UNIQUE(boat_spec_id, feature)
);

CREATE TABLE boat_exterior_features (
    id BIGSERIAL PRIMARY KEY,
    boat_spec_id BIGINT NOT NULL REFERENCES boat_specifications(id) ON DELETE CASCADE,
    feature VARCHAR(50) NOT NULL,

    CONSTRAINT unique_exterior_feature_per_boat UNIQUE(boat_spec_id, feature)
);

CREATE TABLE boat_equipment (
    id BIGSERIAL PRIMARY KEY,
    boat_spec_id BIGINT NOT NULL REFERENCES boat_specifications(id) ON DELETE CASCADE,
    equipment VARCHAR(50) NOT NULL,

    CONSTRAINT unique_equipment_per_boat UNIQUE(boat_spec_id, equipment)
);

-- Indexes for boat specifications
CREATE INDEX idx_boat_specs_ad_id ON boat_specifications(ad_id);
CREATE INDEX idx_boat_specs_brand ON boat_specifications(brand);
CREATE INDEX idx_boat_specs_boat_type ON boat_specifications(boat_type);
CREATE INDEX idx_boat_specs_year ON boat_specifications(year);
CREATE INDEX idx_boat_specs_horsepower ON boat_specifications(horsepower);
CREATE INDEX idx_boat_specs_length ON boat_specifications(length);

-- Feature table indexes
CREATE INDEX idx_boat_interior_features_boat_spec_id ON boat_interior_features(boat_spec_id);
CREATE INDEX idx_boat_exterior_features_boat_spec_id ON boat_exterior_features(boat_spec_id);
CREATE INDEX idx_boat_equipment_boat_spec_id ON boat_equipment(boat_spec_id);