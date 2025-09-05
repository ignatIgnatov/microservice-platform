-- V1__Create_core_schema.sql
-- Complete core schema with all tables and relationships

-- =============================================================================
-- BRANDS TABLE
-- =============================================================================
CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN (
        'MOTOR_BOATS', 'SAILBOATS', 'KAYAKS', 'JET_SKIS', 'TRAILERS',
        'MARINE_ELECTRONICS', 'ENGINES', 'FISHING', 'WATER_SPORTS',
        'MARINE_ACCESSORIES', 'PARTS', 'SERVICES'
    )),
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_brand_category UNIQUE (name, category)
);

-- =============================================================================
-- MAIN ADS TABLE
-- =============================================================================
CREATE TABLE ads (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(30) NOT NULL CHECK (LENGTH(title) >= 5),
    description TEXT NOT NULL CHECK (LENGTH(description) >= 20 AND LENGTH(description) <= 2000),
    quick_description VARCHAR(210) CHECK (quick_description IS NULL OR (LENGTH(quick_description) >= 20 AND LENGTH(quick_description) <= 210)),
    category VARCHAR(50) NOT NULL CHECK (category IN (
        'BOATS_AND_YACHTS', 'JET_SKIS', 'TRAILERS', 'MARINE_ELECTRONICS',
        'ENGINES', 'FISHING', 'WATER_SPORTS', 'PARTS', 'MARINE_ACCESSORIES',
        'SERVICES', 'RENTALS'
    )),
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    views_count INTEGER DEFAULT 0 CHECK (views_count >= 0),
    featured BOOLEAN DEFAULT false,
    approval_status VARCHAR(20) DEFAULT 'APPROVED',
    rejection_reason TEXT,
    approved_by_user_id VARCHAR(100),
    approved_at TIMESTAMP,
    archived BOOLEAN DEFAULT false,
    archived_at TIMESTAMP,
    edit_count INTEGER DEFAULT 0,
    last_edited_at TIMESTAMP,
    contact_person_name VARCHAR(100),
    contact_phone VARCHAR(20),

    CONSTRAINT valid_price_for_fixed_type CHECK (price_type != 'FIXED_PRICE' OR price_amount IS NOT NULL)
);

-- =============================================================================
-- BOAT SPECIFICATIONS
-- =============================================================================
CREATE TABLE boat_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    boat_type VARCHAR(50) NOT NULL CHECK (boat_type IN (
        'ALL', 'MOTOR_BOAT', 'MOTOR_YACHT', 'SAILING_BOAT', 'SAILING_YACHT',
        'INFLATABLE_BOAT', 'SHIP', 'CANOE', 'PONTOON'
    )),
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
    boat_purpose VARCHAR(20) NOT NULL DEFAULT 'ALL' CHECK (boat_purpose IN ('ALL', 'FISHING', 'BEACH', 'WATER_SPORTS', 'WORK')),
    water_type VARCHAR(20) NOT NULL DEFAULT 'ALL' CHECK (water_type IN ('ALL', 'FRESHWATER', 'SALTWATER')),
    engine_hours INTEGER NOT NULL DEFAULT 0 CHECK (engine_hours >= 0),
    located_in_bulgaria BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_boat_spec_per_ad UNIQUE(ad_id)
);

-- Boat feature tables
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

-- =============================================================================
-- JETSKI SPECIFICATIONS
-- =============================================================================
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_jetski_spec_per_ad UNIQUE(ad_id)
);

-- =============================================================================
-- TRAILER SPECIFICATIONS
-- =============================================================================
CREATE TABLE trailer_specifications (
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
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    suspension_type VARCHAR(50) CHECK (suspension_type IN ('DOUBLE_TORSION', 'TORSION', 'LEAF_SPRING', 'RIGID')),
    keel_rollers VARCHAR(30) CHECK (keel_rollers IN ('ALL', 'TWO_ROLLERS', 'THREE_ROLLERS', 'FOUR_ROLLERS', 'FIVE_ROLLERS', 'MULTIPLE_ROLLERS')),
    in_warranty BOOLEAN NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_trailer_spec_per_ad UNIQUE(ad_id)
);

CREATE TABLE trailer_specifications_features (
    id BIGSERIAL PRIMARY KEY,
    trailer_spec_id BIGINT NOT NULL REFERENCES trailer_specifications(id) ON DELETE CASCADE,
    feature VARCHAR(100) NOT NULL,
    CONSTRAINT unique_trailer_feature UNIQUE(trailer_spec_id, feature)
);

-- =============================================================================
-- ENGINE SPECIFICATIONS
-- =============================================================================
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_engine_spec_per_ad UNIQUE(ad_id)
);

-- =============================================================================
-- MARINE ELECTRONICS SPECIFICATIONS
-- =============================================================================
CREATE TABLE marine_electronics_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    electronics_type VARCHAR(50) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    year INTEGER CHECK (year IS NULL OR (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5)),
    in_warranty BOOLEAN,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),
    working_frequency VARCHAR(20),
    depth_range VARCHAR(20),
    screen_size VARCHAR(20),
    probe_included BOOLEAN,
    screen_type VARCHAR(30),
    gps_integrated BOOLEAN,
    bulgarian_language BOOLEAN,
    power VARCHAR(10),
    frequency VARCHAR(20),
    material VARCHAR(50),
    range_length VARCHAR(20),
    mounting VARCHAR(30),
    thrust INTEGER CHECK (thrust IS NULL OR thrust >= 0),
    voltage VARCHAR(10),
    tube_length VARCHAR(20),
    control_type VARCHAR(20),
    mounting_type VARCHAR(20),
    motor_type VARCHAR(20),
    water_resistance VARCHAR(30),
    weight VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_marine_electronics_spec_per_ad UNIQUE(ad_id)
);

-- =============================================================================
-- OTHER SPECIFICATION TABLES
-- =============================================================================

-- Fishing specifications
CREATE TABLE fishing_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    fishing_type VARCHAR(50) NOT NULL,
    brand VARCHAR(100),
    fishing_technique VARCHAR(30) NOT NULL,
    target_fish VARCHAR(50) NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_fishing_spec_per_ad UNIQUE(ad_id)
);

-- Water sports specifications
CREATE TABLE water_sports_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    water_sports_type VARCHAR(50) NOT NULL CHECK (water_sports_type IN (
        'ALL', 'SURF', 'SUP', 'DIVING', 'WATER_SKIING', 'PARAGLIDING',
        'WAKEBOARD', 'WINDSURF', 'JETPACK', 'FLYBOARD', 'SAILING',
        'ROWING', 'WATER_SPORTS_CLOTHING'
    )),
    brand VARCHAR(100),
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_water_sports_spec_per_ad UNIQUE(ad_id)
);

-- Marine accessories specifications
CREATE TABLE marine_accessories_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    accessory_type VARCHAR(50) NOT NULL CHECK (accessory_type IN (
        'ALL', 'LIFE_JACKETS', 'COSMETICS_CHEMICALS', 'HYDROFOIL', 'FENDERS_BUOYS',
        'OARS', 'COOLER_BAGS', 'INSTRUMENTS', 'COMPASSES', 'COVERS', 'TARPS',
        'KNIVES', 'ANCHORS', 'ROPES', 'CONSOLES', 'SEATS_CUSHIONS', 'OTHER'
    )),
    brand VARCHAR(100),
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_marine_accessories_spec_per_ad UNIQUE(ad_id)
);

-- Parts specifications
CREATE TABLE parts_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    part_type VARCHAR(50) NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('ALL', 'NEW', 'USED', 'FOR_PARTS')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_parts_spec_per_ad UNIQUE(ad_id)
);

-- Services specifications
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
    supported_brands TEXT,
    supported_materials TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_services_spec_per_ad UNIQUE(ad_id)
);

-- Rentals specifications
CREATE TABLE rentals_specifications (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    rental_type VARCHAR(50) NOT NULL CHECK (rental_type IN (
        'ALL', 'MOTOR_BOAT', 'MOTOR_YACHT', 'CATAMARAN',
        'SAILING_BOAT_YACHT', 'JET', 'WATER_SPORTS'
    )),
    license_required BOOLEAN NOT NULL,
    management_type VARCHAR(30) NOT NULL CHECK (management_type IN ('ALL', 'SELF_DRIVE', 'WITH_CAPTAIN')),
    number_of_people INTEGER NOT NULL CHECK (number_of_people > 0),
    service_type VARCHAR(30) NOT NULL CHECK (service_type IN ('ALL', 'SHARED_TRIP', 'PRIVATE_RENTAL')),
    company_name VARCHAR(200) NOT NULL,
    description TEXT CHECK (description IS NULL OR LENGTH(description) <= 2000),
    contact_phone VARCHAR(20) NOT NULL,
    contact_email VARCHAR(100),
    address VARCHAR(500),
    website VARCHAR(200),
    max_price DECIMAL(10,2) NOT NULL CHECK (max_price >= 0),
    price_specification VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_rentals_spec_per_ad UNIQUE(ad_id)
);

-- =============================================================================
-- AD IMAGES TABLE
-- =============================================================================
CREATE TABLE ad_images (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    s3_key VARCHAR(500) NOT NULL UNIQUE,
    s3_url VARCHAR(1000) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    width INTEGER,
    height INTEGER,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT idx_ad_display_order UNIQUE (ad_id, display_order)
);