-- V3__Create_jetski_specifications.sql
-- Jet ski specifications table

CREATE TABLE jetski_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    modification VARCHAR(200),
    is_registered BOOLEAN NOT NULL,
    horsepower INTEGER NOT NULL CHECK (horsepower > 0 AND horsepower <= 1000),
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    weight DECIMAL(6,2) NOT NULL CHECK (weight > 0),
    fuel_capacity DECIMAL(6,2) NOT NULL CHECK (fuel_capacity >= 0),
    operating_hours INTEGER NOT NULL CHECK (operating_hours >= 0 AND operating_hours <= 50000),
    fuel_type VARCHAR(20) NOT NULL CHECK (fuel_type IN ('ALL', 'PETROL', 'DIESEL', 'GAS')),
    trailer_included BOOLEAN NOT NULL,
    in_warranty BOOLEAN NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    CONSTRAINT unique_jetski_spec_per_ad UNIQUE(ad_id)
);

-- Indexes for jetski specifications
CREATE INDEX idx_jetski_specs_ad_id ON jetski_specifications(ad_id);
CREATE INDEX idx_jetski_specs_brand ON jetski_specifications(brand);
CREATE INDEX idx_jetski_specs_year ON jetski_specifications(year);
CREATE INDEX idx_jetski_specs_horsepower ON jetski_specifications(horsepower);