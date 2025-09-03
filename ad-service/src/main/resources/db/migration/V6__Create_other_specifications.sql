-- V6__Create_other_specifications.sql
-- Marine Electronics, Fishing, Parts, and Services specifications

-- Marine Electronics Specifications Table
CREATE TABLE marine_electronics_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    electronics_type VARCHAR(50) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    year INTEGER CHECK (year IS NULL OR (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5)),
    in_warranty BOOLEAN,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    -- Sonar specific fields
    working_frequency VARCHAR(20),
    depth_range VARCHAR(20),
    screen_size VARCHAR(20),
    probe_included BOOLEAN,
    screen_type VARCHAR(30),
    gps_integrated BOOLEAN,
    bulgarian_language BOOLEAN,

    -- Probe specific fields
    power VARCHAR(10),
    frequency VARCHAR(20),
    material VARCHAR(50),
    range_length VARCHAR(20),
    mounting VARCHAR(30),

    -- Trolling motor specific fields
    thrust INTEGER CHECK (thrust IS NULL OR thrust >= 0),
    voltage VARCHAR(10),
    tube_length VARCHAR(20),
    control_type VARCHAR(20),
    mounting_type VARCHAR(20),
    motor_type VARCHAR(20),
    water_resistance VARCHAR(30),
    weight VARCHAR(20),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fishing Specifications Table
CREATE TABLE fishing_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    fishing_type VARCHAR(50) NOT NULL,
    brand VARCHAR(100),
    fishing_technique VARCHAR(30) NOT NULL,
    target_fish VARCHAR(50) NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Parts Specifications Table
CREATE TABLE parts_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    part_type VARCHAR(50) NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Services Specifications Table
CREATE TABLE services_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    service_type VARCHAR(50) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    is_authorized_service BOOLEAN,
    is_official_representative BOOLEAN,
    description TEXT,
    contact_phone VARCHAR(20) NOT NULL,
    contact_phone2 VARCHAR(20),
    contact_email VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    website VARCHAR(200),
    supported_brands TEXT, -- comma-separated values
    supported_materials TEXT, -- comma-separated enum values

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for marine electronics specifications
CREATE INDEX idx_marine_electronics_specifications_ad_id ON marine_electronics_specifications(ad_id);
CREATE INDEX idx_marine_electronics_specifications_electronics_type ON marine_electronics_specifications(electronics_type);
CREATE INDEX idx_marine_electronics_specifications_brand ON marine_electronics_specifications(brand);

-- Indexes for fishing specifications
CREATE INDEX idx_fishing_specifications_ad_id ON fishing_specifications(ad_id);
CREATE INDEX idx_fishing_specifications_fishing_type ON fishing_specifications(fishing_type);
CREATE INDEX idx_fishing_specifications_fishing_technique ON fishing_specifications(fishing_technique);
CREATE INDEX idx_fishing_specifications_target_fish ON fishing_specifications(target_fish);

-- Indexes for parts specifications
CREATE INDEX idx_parts_specifications_ad_id ON parts_specifications(ad_id);
CREATE INDEX idx_parts_specifications_part_type ON parts_specifications(part_type);

-- Indexes for services specifications
CREATE INDEX idx_services_specifications_ad_id ON services_specifications(ad_id);
CREATE INDEX idx_services_specifications_service_type ON services_specifications(service_type);
CREATE INDEX idx_services_specifications_company_name ON services_specifications(company_name);