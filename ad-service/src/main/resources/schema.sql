-- Main database schema for QHTI.BG Boat Marketplace

-- Drop existing tables if they exist (for development only)
-- DROP TABLE IF EXISTS boat_equipment CASCADE;
-- DROP TABLE IF EXISTS boat_exterior_features CASCADE;
-- DROP TABLE IF EXISTS boat_interior_features CASCADE;
-- DROP TABLE IF EXISTS trailer_specifications_features CASCADE;
-- DROP TABLE IF EXISTS engine_specifications CASCADE;
-- DROP TABLE IF EXISTS marine_electronics_specifications CASCADE;
-- DROP TABLE IF EXISTS fishing_specifications CASCADE;
-- DROP TABLE IF EXISTS parts_specifications CASCADE;
-- DROP TABLE IF EXISTS service_specifications CASCADE;
-- DROP TABLE IF EXISTS trailer_specifications CASCADE;
-- DROP TABLE IF EXISTS jetski_specifications CASCADE;
-- DROP TABLE IF EXISTS boat_specifications CASCADE;
-- DROP TABLE IF EXISTS ads CASCADE;

-- Main ads table
CREATE TABLE IF NOT EXISTS ads (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(30) NOT NULL CHECK (LENGTH(title) >= 5),
    description TEXT NOT NULL CHECK (LENGTH(description) >= 20 AND LENGTH(description) <= 2000),
    quick_description VARCHAR(210),
    category VARCHAR(50) NOT NULL CHECK (category IN ('BOATS_AND_YACHTS', 'JET_SKIS', 'TRAILERS', 'MARINE_ELECTRONICS', 'ENGINES', 'FISHING', 'PARTS', 'SERVICES')),
    price_amount DECIMAL(12,2) CHECK (price_amount >= 0),
    price_type VARCHAR(20) NOT NULL CHECK (price_type IN ('FIXED_PRICE', 'FREE', 'NEGOTIABLE', 'BARTER')),
    including_vat BOOLEAN DEFAULT false,
    location VARCHAR(200) NOT NULL,
    ad_type VARCHAR(20) NOT NULL CHECK (ad_type IN ('FROM_PRIVATE', 'FROM_COMPANY')),
    user_email VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    user_first_name VARCHAR(100),
    user_last_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    views_count INTEGER DEFAULT 0 CHECK (views_count >= 0),
    featured BOOLEAN DEFAULT false,
    approval_status VARCHAR(20) DEFAULT 'APPROVED',
    rejection_reason TEXT,
    approved_by_user_id VARCHAR(100),
    approved_at TIMESTAMP,

    -- Constraints
    CONSTRAINT valid_price_for_fixed_type
        CHECK (price_type != 'FIXED_PRICE' OR price_amount IS NOT NULL)
);

-- Boat specifications table
CREATE TABLE IF NOT EXISTS boat_specifications (
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
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5), -- Changed to +5 years
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

-- Jet ski specifications table
CREATE TABLE IF NOT EXISTS jetski_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    modification VARCHAR(200),
    is_registered BOOLEAN NOT NULL,
    horsepower INTEGER NOT NULL CHECK (horsepower > 0 AND horsepower <= 1000),
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5), -- Changed to +5 years
    weight DECIMAL(6,2) NOT NULL CHECK (weight > 0),
    fuel_capacity DECIMAL(6,2) NOT NULL CHECK (fuel_capacity >= 0),
    operating_hours INTEGER NOT NULL CHECK (operating_hours >= 0 AND operating_hours <= 50000),
    fuel_type VARCHAR(20) NOT NULL CHECK (fuel_type IN ('ALL', 'PETROL', 'DIESEL', 'GAS')),
    trailer_included BOOLEAN NOT NULL,
    in_warranty BOOLEAN NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    CONSTRAINT unique_jetski_spec_per_ad UNIQUE(ad_id)
);

-- Trailer specifications table
CREATE TABLE IF NOT EXISTS trailer_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    trailer_type VARCHAR(50) NOT NULL CHECK (trailer_type IN ('ALL', 'JET_TRAILER', 'BOAT_TRAILER')),
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    axle_count VARCHAR(20) NOT NULL CHECK (axle_count IN ('ALL', 'SINGLE', 'DOUBLE', 'TRIPLE')),
    is_registered BOOLEAN NOT NULL,
    own_weight DECIMAL(8,2) CHECK (own_weight >= 0),
    load_capacity DECIMAL(8,2) NOT NULL CHECK (load_capacity > 0),
    length DECIMAL(5,2) NOT NULL CHECK (length > 0),
    width DECIMAL(5,2) NOT NULL CHECK (width > 0),
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5), -- Changed to +5 years
    suspension_type VARCHAR(50) CHECK (suspension_type IN ('DOUBLE_TORSION', 'TORSION', 'LEAF_SPRING', 'RIGID')),
    keel_rollers VARCHAR(30) CHECK (keel_rollers IN ('ALL', 'TWO_ROLLERS', 'THREE_ROLLERS', 'FOUR_ROLLERS', 'FIVE_ROLLERS', 'MULTIPLE_ROLLERS')),
    in_warranty BOOLEAN NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    CONSTRAINT unique_trailer_spec_per_ad UNIQUE(ad_id)
);

-- Engine specifications table
CREATE TABLE IF NOT EXISTS engine_specifications (
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
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5), -- Changed to +5 years
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

-- Feature tables for many-to-many relationships
CREATE TABLE IF NOT EXISTS boat_interior_features (
    id BIGSERIAL PRIMARY KEY,
    boat_spec_id BIGINT NOT NULL REFERENCES boat_specifications(id) ON DELETE CASCADE,
    feature VARCHAR(50) NOT NULL,

    CONSTRAINT unique_interior_feature_per_boat UNIQUE(boat_spec_id, feature)
);

CREATE TABLE IF NOT EXISTS boat_exterior_features (
    id BIGSERIAL PRIMARY KEY,
    boat_spec_id BIGINT NOT NULL REFERENCES boat_specifications(id) ON DELETE CASCADE,
    feature VARCHAR(50) NOT NULL,

    CONSTRAINT unique_exterior_feature_per_boat UNIQUE(boat_spec_id, feature)
);

CREATE TABLE IF NOT EXISTS boat_equipment (
    id BIGSERIAL PRIMARY KEY,
    boat_spec_id BIGINT NOT NULL REFERENCES boat_specifications(id) ON DELETE CASCADE,
    equipment VARCHAR(50) NOT NULL,

    CONSTRAINT unique_equipment_per_boat UNIQUE(boat_spec_id, equipment)
);

CREATE TABLE IF NOT EXISTS trailer_specifications_features (
    id BIGSERIAL PRIMARY KEY,
    trailer_spec_id BIGINT NOT NULL REFERENCES trailer_specifications(id) ON DELETE CASCADE,
    feature VARCHAR(100) NOT NULL,

    CONSTRAINT unique_trailer_feature UNIQUE(trailer_spec_id, feature)
);

-- Marine Electronics Specifications Table
CREATE TABLE IF NOT EXISTS marine_electronics_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    electronics_type VARCHAR(50) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    year INTEGER CHECK (year IS NULL OR (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5)), -- Fixed constraint
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
CREATE TABLE IF NOT EXISTS fishing_specifications (
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
CREATE TABLE IF NOT EXISTS parts_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    part_type VARCHAR(50) NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Services Specifications Table
CREATE TABLE IF NOT EXISTS services_specifications (
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

-- Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_ads_category ON ads(category);
CREATE INDEX IF NOT EXISTS idx_ads_price_type ON ads(price_type);
CREATE INDEX IF NOT EXISTS idx_ads_price_amount ON ads(price_amount) WHERE price_type = 'FIXED_PRICE';
CREATE INDEX IF NOT EXISTS idx_ads_location ON ads(location);
CREATE INDEX IF NOT EXISTS idx_ads_user_email ON ads(user_email);
CREATE INDEX IF NOT EXISTS idx_ads_user_id ON ads(user_id);
CREATE INDEX IF NOT EXISTS idx_ads_active ON ads(active);
CREATE INDEX IF NOT EXISTS idx_ads_created_at ON ads(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ads_views_count ON ads(views_count DESC);
CREATE INDEX IF NOT EXISTS idx_ads_featured ON ads(featured);

-- Composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_ads_category_active_created ON ads(category, active, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ads_location_active_created ON ads(location, active, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ads_user_active_created ON ads(user_email, active, created_at DESC);

-- Specification table indexes
CREATE INDEX IF NOT EXISTS idx_boat_specs_ad_id ON boat_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_boat_specs_brand ON boat_specifications(brand);
CREATE INDEX IF NOT EXISTS idx_boat_specs_boat_type ON boat_specifications(boat_type);
CREATE INDEX IF NOT EXISTS idx_boat_specs_year ON boat_specifications(year);
CREATE INDEX IF NOT EXISTS idx_boat_specs_horsepower ON boat_specifications(horsepower);
CREATE INDEX IF NOT EXISTS idx_boat_specs_length ON boat_specifications(length);

CREATE INDEX IF NOT EXISTS idx_jetski_specs_ad_id ON jetski_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_jetski_specs_brand ON jetski_specifications(brand);
CREATE INDEX IF NOT EXISTS idx_jetski_specs_year ON jetski_specifications(year);
CREATE INDEX IF NOT EXISTS idx_jetski_specs_horsepower ON jetski_specifications(horsepower);

CREATE INDEX IF NOT EXISTS idx_trailer_specs_ad_id ON trailer_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_trailer_specs_brand ON trailer_specifications(brand);
CREATE INDEX IF NOT EXISTS idx_trailer_specs_load_capacity ON trailer_specifications(load_capacity);

CREATE INDEX IF NOT EXISTS idx_engine_specs_ad_id ON engine_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_engine_specs_brand ON engine_specifications(brand);
CREATE INDEX IF NOT EXISTS idx_engine_specs_horsepower ON engine_specifications(horsepower);
CREATE INDEX IF NOT EXISTS idx_engine_specs_year ON engine_specifications(year);

-- Feature table indexes
CREATE INDEX IF NOT EXISTS idx_boat_interior_features_boat_spec_id ON boat_interior_features(boat_spec_id);
CREATE INDEX IF NOT EXISTS idx_boat_exterior_features_boat_spec_id ON boat_exterior_features(boat_spec_id);
CREATE INDEX IF NOT EXISTS idx_boat_equipment_boat_spec_id ON boat_equipment(boat_spec_id);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_marine_electronics_specifications_ad_id ON marine_electronics_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_marine_electronics_specifications_electronics_type ON marine_electronics_specifications(electronics_type);
CREATE INDEX IF NOT EXISTS idx_marine_electronics_specifications_brand ON marine_electronics_specifications(brand);

CREATE INDEX IF NOT EXISTS idx_fishing_specifications_ad_id ON fishing_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_fishing_specifications_fishing_type ON fishing_specifications(fishing_type);
CREATE INDEX IF NOT EXISTS idx_fishing_specifications_fishing_technique ON fishing_specifications(fishing_technique);
CREATE INDEX IF NOT EXISTS idx_fishing_specifications_target_fish ON fishing_specifications(target_fish);

CREATE INDEX IF NOT EXISTS idx_parts_specifications_ad_id ON parts_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_parts_specifications_part_type ON parts_specifications(part_type);

CREATE INDEX IF NOT EXISTS idx_services_specifications_ad_id ON services_specifications(ad_id);
CREATE INDEX IF NOT EXISTS idx_services_specifications_service_type ON services_specifications(service_type);
CREATE INDEX IF NOT EXISTS idx_services_specifications_company_name ON services_specifications(company_name);

CREATE INDEX IF NOT EXISTS idx_ads_approval_status ON ads(approval_status);
CREATE INDEX IF NOT EXISTS idx_ads_approved_by ON ads(approved_by_user_id);
CREATE INDEX IF NOT EXISTS idx_ads_approved_at ON ads(approved_at);

-- Text search indexes (if using PostgreSQL full-text search)
CREATE INDEX IF NOT EXISTS idx_ads_text_search ON ads USING gin(to_tsvector('english', title || ' ' || description));

-- Views for common queries
CREATE OR REPLACE VIEW active_ads AS
SELECT * FROM ads WHERE active = true;

CREATE OR REPLACE VIEW featured_ads AS
SELECT * FROM ads WHERE active = true AND featured = true;

COMMIT;