-- V1__Create_base_tables.sql
-- Core tables: brands and ads

-- Create brands table for brand validation
CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('MOTOR_BOATS', 'SAILBOATS', 'KAYAKS')),
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_brand_category UNIQUE (name, category)
);

-- Main ads table
CREATE TABLE ads (
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
    archived BOOLEAN DEFAULT false,
    archived_at TIMESTAMP NULL,
    edit_count INTEGER DEFAULT 0,
    last_edited_at TIMESTAMP NULL,

    -- Constraints
    CONSTRAINT valid_price_for_fixed_type
        CHECK (price_type != 'FIXED_PRICE' OR price_amount IS NOT NULL)
);

-- Basic indexes for brands table
CREATE INDEX idx_brands_category ON brands(category);
CREATE INDEX idx_brands_active ON brands(active);
CREATE INDEX idx_brands_display_order ON brands(category, display_order);
CREATE INDEX idx_brands_name ON brands(name);

-- Basic indexes for ads table
CREATE INDEX idx_ads_category ON ads(category);
CREATE INDEX idx_ads_user_id ON ads(user_id);
CREATE INDEX idx_ads_active ON ads(active);
CREATE INDEX idx_ads_created_at ON ads(created_at DESC);
CREATE INDEX idx_ads_user_email ON ads(user_email);