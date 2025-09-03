-- V5__Create_engine_specifications.sql
-- Engine specifications table

CREATE TABLE engine_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    engine_type VARCHAR(30) NOT NULL CHECK (engine_type IN ('ALL', 'OUTBOARD', 'INBOARD')),
    brand VARCHAR(100) NOT NULL,
    modification VARCHAR(200),
    stroke_type VARCHAR(20) NOT NULL CHECK (stroke_type IN ('ALL', 'TWO_STROKE', 'FOUR_STROKE')),
    in_warranty BOOLEAN NOT NULL,
    horsepower INTEGER NOT NULL CHECK (horsepower > 0 AND horsepower <= 10000),
    operating_hours INTEGER NOT NULL CHECK (operating_hours >= 0),
    cylinders INTEGER CHECK (cylinders >= 1 AND cylinders <= 50),
    displacement_cc INTEGER CHECK (displacement_cc > 0),
    rpm INTEGER CHECK (rpm > 0),
    weight DECIMAL(6,2) CHECK (weight > 0),
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    fuel_capacity DECIMAL(6,2) NOT NULL CHECK (fuel_capacity >= 0),
    ignition_type VARCHAR(20) NOT NULL CHECK (ignition_type IN ('ALL', 'MANUAL', 'ELECTRIC')),
    control_type VARCHAR(20) NOT NULL CHECK (control_type IN ('ALL', 'HANDLE', 'HYDRAULIC')),
    shaft_length VARCHAR(10) NOT NULL CHECK (shaft_length IN ('ALL', 'S', 'M', 'L', 'XL')),
    fuel_type VARCHAR(20) NOT NULL CHECK (fuel_type IN ('ALL', 'PETROL', 'DIESEL', 'LPG')),
    engine_system_type VARCHAR(20) NOT NULL CHECK (engine_system_type IN ('ALL', 'CARBURETOR', 'EFI')),
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),
    color VARCHAR(20) NOT NULL CHECK (color IN ('BLACK', 'GREY', 'YELLOW', 'WHITE', 'BLUE', 'ORANGE', 'RED', 'GREEN', 'PURPLE')),

    CONSTRAINT unique_engine_spec_per_ad UNIQUE(ad_id)
);

-- Indexes for engine specifications
CREATE INDEX idx_engine_specs_ad_id ON engine_specifications(ad_id);
CREATE INDEX idx_engine_specs_brand ON engine_specifications(brand);
CREATE INDEX idx_engine_specs_horsepower ON engine_specifications(horsepower);
CREATE INDEX idx_engine_specs_year ON engine_specifications(year);